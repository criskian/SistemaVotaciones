@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  votacion startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and VOTACION_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\votacion-1.0-SNAPSHOT.jar;%APP_HOME%\lib\reliablemsg-1.0-SNAPSHOT.jar;%APP_HOME%\lib\common-1.0-SNAPSHOT.jar;%APP_HOME%\lib\interfaces-1.0-SNAPSHOT.jar;%APP_HOME%\lib\icegrid-3.7.10.jar;%APP_HOME%\lib\glacier2-3.7.10.jar;%APP_HOME%\lib\icebox-3.7.10.jar;%APP_HOME%\lib\icepatch2-3.7.10.jar;%APP_HOME%\lib\icestorm-3.7.10.jar;%APP_HOME%\lib\ice-3.7.10.jar;%APP_HOME%\lib\ice-compat-3.7.10.jar;%APP_HOME%\lib\gson-2.8.9.jar;%APP_HOME%\lib\okhttp-4.9.1.jar;%APP_HOME%\lib\postgrest-kt-jvm-1.4.7.jar;%APP_HOME%\lib\activemq-client-5.16.3.jar;%APP_HOME%\lib\ktor-client-cio-jvm-2.3.7.jar;%APP_HOME%\lib\okio-jvm-2.8.0.jar;%APP_HOME%\lib\kotlin-reflect-1.9.20.jar;%APP_HOME%\lib\gotrue-kt-jvm-1.4.7.jar;%APP_HOME%\lib\supabase-kt-jvm-1.4.7.jar;%APP_HOME%\lib\ktor-client-content-negotiation-jvm-2.3.5.jar;%APP_HOME%\lib\ktor-client-core-jvm-2.3.7.jar;%APP_HOME%\lib\ktor-http-cio-jvm-2.3.7.jar;%APP_HOME%\lib\ktor-websocket-serialization-jvm-2.3.7.jar;%APP_HOME%\lib\ktor-serialization-kotlinx-json-jvm-2.3.5.jar;%APP_HOME%\lib\ktor-serialization-kotlinx-jvm-2.3.5.jar;%APP_HOME%\lib\ktor-serialization-jvm-2.3.7.jar;%APP_HOME%\lib\ktor-websockets-jvm-2.3.7.jar;%APP_HOME%\lib\ktor-network-tls-jvm-2.3.7.jar;%APP_HOME%\lib\ktor-events-jvm-2.3.7.jar;%APP_HOME%\lib\ktor-http-jvm-2.3.7.jar;%APP_HOME%\lib\ktor-network-jvm-2.3.7.jar;%APP_HOME%\lib\ktor-utils-jvm-2.3.7.jar;%APP_HOME%\lib\multiplatform-settings-no-arg-jvm-1.1.0.jar;%APP_HOME%\lib\multiplatform-settings-coroutines-jvm-1.1.0.jar;%APP_HOME%\lib\krypto-jvm-4.0.10.jar;%APP_HOME%\lib\ktor-io-jvm-2.3.7.jar;%APP_HOME%\lib\kotlinx-datetime-jvm-0.4.0.jar;%APP_HOME%\lib\kermit-jvm-2.0.2.jar;%APP_HOME%\lib\atomicfu-jvm-0.22.0.jar;%APP_HOME%\lib\multiplatform-settings-jvm-1.1.0.jar;%APP_HOME%\lib\kermit-core-jvm-2.0.2.jar;%APP_HOME%\lib\kotlinx-serialization-core-jvm-1.5.1.jar;%APP_HOME%\lib\kotlinx-serialization-json-jvm-1.5.1.jar;%APP_HOME%\lib\kotlinx-coroutines-jdk8-1.7.3.jar;%APP_HOME%\lib\kotlinx-coroutines-slf4j-1.7.3.jar;%APP_HOME%\lib\kotlinx-coroutines-core-jvm-1.7.3.jar;%APP_HOME%\lib\javalin-5.6.3.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.9.10.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.9.10.jar;%APP_HOME%\lib\kotlin-test-junit-1.9.10.jar;%APP_HOME%\lib\kotlin-test-1.9.10.jar;%APP_HOME%\lib\kotlin-stdlib-1.9.20.jar;%APP_HOME%\lib\websocket-jetty-server-11.0.17.jar;%APP_HOME%\lib\jetty-webapp-11.0.17.jar;%APP_HOME%\lib\websocket-servlet-11.0.17.jar;%APP_HOME%\lib\jetty-servlet-11.0.17.jar;%APP_HOME%\lib\jetty-security-11.0.17.jar;%APP_HOME%\lib\websocket-core-server-11.0.17.jar;%APP_HOME%\lib\jetty-server-11.0.17.jar;%APP_HOME%\lib\websocket-jetty-common-11.0.17.jar;%APP_HOME%\lib\websocket-core-common-11.0.17.jar;%APP_HOME%\lib\jetty-http-11.0.17.jar;%APP_HOME%\lib\jetty-io-11.0.17.jar;%APP_HOME%\lib\jetty-xml-11.0.17.jar;%APP_HOME%\lib\jetty-util-11.0.17.jar;%APP_HOME%\lib\slf4j-api-2.0.7.jar;%APP_HOME%\lib\geronimo-jms_1.1_spec-1.1.1.jar;%APP_HOME%\lib\hawtbuf-1.11.jar;%APP_HOME%\lib\geronimo-j2ee-management_1.1_spec-1.0.1.jar;%APP_HOME%\lib\annotations-24.0.1.jar;%APP_HOME%\lib\websocket-jetty-api-11.0.17.jar;%APP_HOME%\lib\jetty-jakarta-servlet-api-5.0.2.jar;%APP_HOME%\lib\junit-4.13.2.jar;%APP_HOME%\lib\hamcrest-core-1.3.jar


@rem Execute votacion
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %VOTACION_OPTS%  -classpath "%CLASSPATH%" com.sistemaelectoral.votacion.VotacionServer %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable VOTACION_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%VOTACION_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
