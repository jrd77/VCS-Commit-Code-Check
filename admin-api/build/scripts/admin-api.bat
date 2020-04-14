@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      http://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  admin-api startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and ADMIN_API_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\admin-api-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\order-openapi-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-settle-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-delivery-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\renter-wz-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-detain-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\cashier-account-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\renter-order-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\renter-mem-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-transport-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\renter-commodity-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\carowner-commodity-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\carowner-order-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\renter-cost-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\parent-order-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\carowner-mem-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\carowner-cost-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\carowner-penalty-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\fee-calculator-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\config-client-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-remote-mem-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-remote-car-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-mq-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-coin-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-flow-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\account-debt-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\account-owner-cost-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\account-owner-income-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\account-platorm-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\account-renter-claim-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\account-renter-deposit-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\account-renter-detain-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\account-renter-rentcost-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\account-renter-stopcost-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\account-renter-wz-deposit-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-wallet-client-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-commons-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\config-common-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\config-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\order-wallet-api-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\javax.servlet-api-4.0.1.jar;%APP_HOME%\lib\fetchBackCarFeeService-api-feign-1.0.1-SNAPSHOT.jar;%APP_HOME%\lib\auto-coin-service-api-1.0.3-SNAPSHOT.jar;%APP_HOME%\lib\memberDetailService-api-1.0.2-SNAPSHOT.jar;%APP_HOME%\lib\autoPayGatewaySecondary-api-1.0.9-SNAPSHOT.jar;%APP_HOME%\lib\auto-wallet-service-api-1.0.2-SNAPSHOT.jar;%APP_HOME%\lib\auto-web-starter-1.0-SNAPSHOT.jar;%APP_HOME%\lib\cat-starter-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\short-url-feign-2.0-SNAPSHOT.jar;%APP_HOME%\lib\spring-cloud-starter-netflix-eureka-client-1.4.7.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-web-1.5.21.RELEASE.jar;%APP_HOME%\lib\mybatis-spring-boot-starter-1.1.1.jar;%APP_HOME%\lib\mysql-connector-java-8.0.13.jar;%APP_HOME%\lib\fastjson-1.2.54.jar;%APP_HOME%\lib\spring-cloud-starter-netflix-hystrix-1.4.7.RELEASE.jar;%APP_HOME%\lib\hystrix-javanica-1.5.12.jar;%APP_HOME%\lib\commons-lang3-3.8.1.jar;%APP_HOME%\lib\auto-car-service-api-2020030401-SNAPSHOT.jar;%APP_HOME%\lib\lombok-1.18.8.jar;%APP_HOME%\lib\spring-boot-starter-actuator-1.5.21.RELEASE.jar;%APP_HOME%\lib\spring-cloud-starter-sleuth-1.3.6.RELEASE.jar;%APP_HOME%\lib\prop-encrypt-spring-boot-starter-0.0.1-SNAPSHOT.jar;%APP_HOME%\lib\spring-boot-starter-aop-1.5.21.RELEASE.jar;%APP_HOME%\lib\apollo-client-0.11.0-SNAPSHOT.jar;%APP_HOME%\lib\spring-rabbit-1.7.13.RELEASE.jar;%APP_HOME%\lib\json-lib-2.4-jdk15.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.8.11.jar;%APP_HOME%\lib\mybatis-typehandlers-jsr310-1.0.1.jar;%APP_HOME%\lib\thumbnailator-0.4.8.jar;%APP_HOME%\lib\auto-doc-1.0-SNAPSHOT.jar;%APP_HOME%\lib\hessian-4.0.38.jar;%APP_HOME%\lib\auto-mq-event-1.0.2-SNAPSHOT.jar;%APP_HOME%\lib\spring-cloud-starter-feign-1.4.7.RELEASE.jar;%APP_HOME%\lib\spring-cloud-starter-openfeign-1.4.7.RELEASE.jar;%APP_HOME%\lib\spring-cloud-starter-netflix-ribbon-1.4.7.RELEASE.jar;%APP_HOME%\lib\spring-cloud-starter-netflix-archaius-1.4.7.RELEASE.jar;%APP_HOME%\lib\spring-cloud-starter-1.3.6.RELEASE.jar;%APP_HOME%\lib\auto-aliyunmq-core-0.0.2.jar;%APP_HOME%\lib\mybatis-spring-boot-autoconfigure-1.1.1.jar;%APP_HOME%\lib\spring-boot-starter-jdbc-1.5.21.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-1.5.21.RELEASE.jar;%APP_HOME%\lib\spring-boot-starter-tomcat-1.5.21.RELEASE.jar;%APP_HOME%\lib\hibernate-validator-5.3.6.Final.jar;%APP_HOME%\lib\spring-boot-actuator-1.5.21.RELEASE.jar;%APP_HOME%\lib\http-client-1.1.1.RELEASE.jar;%APP_HOME%\lib\jackson-module-parameter-names-2.8.11.jar;%APP_HOME%\lib\jackson-datatype-jdk8-2.8.11.jar;%APP_HOME%\lib\ribbon-eureka-2.2.5.jar;%APP_HOME%\lib\eureka-core-1.7.2.jar;%APP_HOME%\lib\eureka-client-1.7.2.jar;%APP_HOME%\lib\netflix-eventbus-0.3.0.jar;%APP_HOME%\lib\ribbon-2.2.5.jar;%APP_HOME%\lib\ribbon-httpclient-2.2.5.jar;%APP_HOME%\lib\ribbon-transport-2.2.5.jar;%APP_HOME%\lib\ribbon-loadbalancer-2.2.5.jar;%APP_HOME%\lib\ribbon-core-2.2.5.jar;%APP_HOME%\lib\feign-hystrix-9.5.0.jar;%APP_HOME%\lib\hystrix-metrics-event-stream-1.5.12.jar;%APP_HOME%\lib\hystrix-serialization-1.5.12.jar;%APP_HOME%\lib\hystrix-core-1.5.12.jar;%APP_HOME%\lib\archaius-core-0.7.4.jar;%APP_HOME%\lib\jackson-module-afterburner-2.8.11.jar;%APP_HOME%\lib\jackson-databind-2.8.11.3.jar;%APP_HOME%\lib\spring-webmvc-4.3.24.RELEASE.jar;%APP_HOME%\lib\spring-web-4.3.24.RELEASE.jar;%APP_HOME%\lib\protobuf-java-3.6.1.jar;%APP_HOME%\lib\apollo-core-0.11.0-SNAPSHOT.jar;%APP_HOME%\lib\cat-client-2.0.0.jar;%APP_HOME%\lib\aliyun-sdk-mns-1.1.4.jar;%APP_HOME%\lib\netflix-infix-0.3.0.jar;%APP_HOME%\lib\gson-2.8.5.jar;%APP_HOME%\lib\spring-cloud-sleuth-core-1.3.6.RELEASE.jar;%APP_HOME%\lib\spring-messaging-4.3.24.RELEASE.jar;%APP_HOME%\lib\spring-cloud-netflix-eureka-client-1.4.7.RELEASE.jar;%APP_HOME%\lib\spring-cloud-netflix-core-1.4.7.RELEASE.jar;%APP_HOME%\lib\spring-boot-autoconfigure-1.5.21.RELEASE.jar;%APP_HOME%\lib\spring-boot-1.5.21.RELEASE.jar;%APP_HOME%\lib\spring-context-4.3.24.RELEASE.jar;%APP_HOME%\lib\spring-aop-4.3.24.RELEASE.jar;%APP_HOME%\lib\aspectjweaver-1.8.14.jar;%APP_HOME%\lib\jasypt-spring-boot-starter-1.7.jar;%APP_HOME%\lib\guice-4.1.0.jar;%APP_HOME%\lib\amqp-client-4.8.3.jar;%APP_HOME%\lib\ehcache-3.2.3.jar;%APP_HOME%\lib\spring-boot-starter-logging-1.5.21.RELEASE.jar;%APP_HOME%\lib\logback-classic-1.1.11.jar;%APP_HOME%\lib\jcl-over-slf4j-1.7.26.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.26.jar;%APP_HOME%\lib\log4j-over-slf4j-1.7.26.jar;%APP_HOME%\lib\rxnetty-contexts-0.4.9.jar;%APP_HOME%\lib\rxnetty-servo-0.4.9.jar;%APP_HOME%\lib\rxnetty-0.4.9.jar;%APP_HOME%\lib\servo-core-0.10.1.jar;%APP_HOME%\lib\servo-internal-0.10.1.jar;%APP_HOME%\lib\netflix-commons-util-0.1.1.jar;%APP_HOME%\lib\netflix-statistics-0.1.1.jar;%APP_HOME%\lib\feign-slf4j-9.5.0.jar;%APP_HOME%\lib\slf4j-api-1.7.26.jar;%APP_HOME%\lib\mybatis-3.4.5.jar;%APP_HOME%\lib\mybatis-spring-1.3.1.jar;%APP_HOME%\lib\spring-jdbc-4.3.24.RELEASE.jar;%APP_HOME%\lib\spring-tx-4.3.24.RELEASE.jar;%APP_HOME%\lib\spring-retry-1.2.4.RELEASE.jar;%APP_HOME%\lib\spring-amqp-1.7.13.RELEASE.jar;%APP_HOME%\lib\commons-beanutils-1.9.3.jar;%APP_HOME%\lib\commons-collections-3.2.2.jar;%APP_HOME%\lib\ezmorph-1.0.6.jar;%APP_HOME%\lib\commons-configuration-1.8.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\commons-httpclient-3.1.jar;%APP_HOME%\lib\spring-beans-4.3.24.RELEASE.jar;%APP_HOME%\lib\spring-expression-4.3.24.RELEASE.jar;%APP_HOME%\lib\spring-core-4.3.24.RELEASE.jar;%APP_HOME%\lib\aliyun-sdk-oss-2.8.3.jar;%APP_HOME%\lib\spring-cloud-commons-1.3.6.RELEASE.jar;%APP_HOME%\lib\httpasyncclient-4.1.4.jar;%APP_HOME%\lib\jersey-apache-client4-1.19.1.jar;%APP_HOME%\lib\httpclient-4.5.8.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\xstream-1.4.10.jar;%APP_HOME%\lib\jackson-annotations-2.8.0.jar;%APP_HOME%\lib\jackson-core-2.8.11.jar;%APP_HOME%\lib\validation-api-1.1.0.Final.jar;%APP_HOME%\lib\swagger-annotations-1.5.10.jar;%APP_HOME%\lib\groovy-all-2.4.17.jar;%APP_HOME%\lib\commons-email-1.3.2.jar;%APP_HOME%\lib\mail-1.4.7.jar;%APP_HOME%\lib\commons-io-2.4.jar;%APP_HOME%\lib\snakeyaml-1.17.jar;%APP_HOME%\lib\tomcat-embed-websocket-8.5.40.jar;%APP_HOME%\lib\tomcat-embed-core-8.5.40.jar;%APP_HOME%\lib\tomcat-embed-el-8.5.40.jar;%APP_HOME%\lib\jboss-logging-3.3.2.Final.jar;%APP_HOME%\lib\classmate-1.3.4.jar;%APP_HOME%\lib\spring-cloud-context-1.3.6.RELEASE.jar;%APP_HOME%\lib\spring-security-rsa-1.0.3.RELEASE.jar;%APP_HOME%\lib\aspectjrt-1.8.14.jar;%APP_HOME%\lib\jasypt-spring-boot-1.7.jar;%APP_HOME%\lib\guava-18.0.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\foundation-service-4.0.0.jar;%APP_HOME%\lib\netty-all-4.0.24.Final.jar;%APP_HOME%\lib\jettison-1.3.7.jar;%APP_HOME%\lib\jersey-client-1.19.1.jar;%APP_HOME%\lib\jersey-core-1.19.1.jar;%APP_HOME%\lib\jsr311-api-1.1.1.jar;%APP_HOME%\lib\woodstox-core-asl-4.4.1.jar;%APP_HOME%\lib\rxjava-1.2.0.jar;%APP_HOME%\lib\xmlpull-1.1.3.1.jar;%APP_HOME%\lib\xpp3_min-1.1.4c.jar;%APP_HOME%\lib\activation-1.1.1.jar;%APP_HOME%\lib\jdom-1.1.jar;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\commons-codec-1.10.jar;%APP_HOME%\lib\cache-api-1.0.0.jar;%APP_HOME%\lib\commons-collections4-4.0.jar;%APP_HOME%\lib\tomcat-annotations-api-8.5.40.jar;%APP_HOME%\lib\tomcat-jdbc-8.5.40.jar;%APP_HOME%\lib\spring-security-crypto-4.2.12.RELEASE.jar;%APP_HOME%\lib\bcpkix-jdk15on-1.55.jar;%APP_HOME%\lib\jasypt-1.9.2.jar;%APP_HOME%\lib\httpcore-nio-4.4.10.jar;%APP_HOME%\lib\httpcore-4.4.11.jar;%APP_HOME%\lib\stax-api-1.0.1.jar;%APP_HOME%\lib\commons-math-2.2.jar;%APP_HOME%\lib\stax-api-1.0-2.jar;%APP_HOME%\lib\stax2-api-3.1.4.jar;%APP_HOME%\lib\feign-core-9.5.0.jar;%APP_HOME%\lib\coupon-api-20200110-SNAPSHOT.jar;%APP_HOME%\lib\logback-core-1.1.11.jar;%APP_HOME%\lib\tomcat-juli-8.5.40.jar;%APP_HOME%\lib\bcprov-jdk15on-1.55.jar;%APP_HOME%\lib\commons-jxpath-1.3.jar;%APP_HOME%\lib\joda-time-2.9.9.jar;%APP_HOME%\lib\antlr-runtime-3.4.jar;%APP_HOME%\lib\HdrHistogram-2.1.9.jar;%APP_HOME%\lib\animal-sniffer-annotation-1.0.jar;%APP_HOME%\lib\asm-5.0.4.jar;%APP_HOME%\lib\stringtemplate-3.2.1.jar;%APP_HOME%\lib\antlr-2.7.7.jar

@rem Execute admin-api
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %ADMIN_API_OPTS%  -classpath "%CLASSPATH%" com.autoyol.doc.parser.autodoc.AutoDocParser %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable ADMIN_API_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%ADMIN_API_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
