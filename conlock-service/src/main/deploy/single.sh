#!/bin/bash

#取当前目录
BASE_PATH=`cd "$(dirname "$0")"; pwd`

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "

#设置debug参数 第二个参数
JAVA_DEBUG_OPTS=""
if [ "$2" = "debug" ]; then
     JAVA_DEBUG_OPTS=" -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006 "
fi

#设置jmx参数 第二个参数
JAVA_JMX_OPTS=""
if [ "$2" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

#设置java运行参数
JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ -n "$BITS" ]; then
    JAVA_MEM_OPTS=" -server -Xmx1g -Xms1g -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
else
    JAVA_MEM_OPTS=" -server -Xms1g -Xmx1g -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi

#引入外部参数配置文件:
SHELL_PARAMS="$BASE_PATH/params.conf"
if [ -f "$SHELL_PARAMS" ]; then
	. $SHELL_PARAMS
fi

#定义变量:
APP_PATH=${APP_PATH:-`dirname "$BASE_PATH"`}
CLASS_PATH=${CLASS_PATH:-$APP_PATH/config:$APP_PATH/lib/*}
JAVA_OPTS=${JAVA_OPTS:-$DEFAULT_JAVA_OPTS}
MAIN_CLASS=${MAIN_CLASS:-"org.willishz.ConlockServiceRunner"}

DATE=`date +"%Y-%m-%d"`

LOGS_DIR=$APP_PATH/logs

if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi

STDOUT_FILE=$LOGS_DIR/stdout.log


exist(){
			if test $( pgrep -f "$MAIN_CLASS $APP_PATH" | wc -l ) -eq 0
			then
				return 1
			else
				return 0
			fi
}

start(){
		if exist; then
				echo "service is already running."
				exit 1
		else
	    	cd $APP_PATH
				nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -cp $CLASS_PATH $MAIN_CLASS $APP_PATH 2>&1 | /usr/local/sbin/cronolog $STDOUT_FILE.%Y-%m-%d >> /dev/null &
				echo "service is started."
		fi
}

stop(){
		runningPID=`pgrep -f "$MAIN_CLASS $APP_PATH"`
		if [ "$runningPID" ]; then
				echo "service pid: $runningPID"
        count=0
        kwait=5
        echo "service is stopping, please wait..."
        kill -15 $runningPID
					until [ `ps --pid $runningPID 2> /dev/null | grep -c $runningPID 2> /dev/null` -eq '0' ] || [ $count -gt $kwait ]
		        do
		            sleep 1
		            let count=$count+1;
		        done

	        if [ $count -gt $kwait ]; then
	            kill -9 $runningPID
	        fi
        clear
        echo "service is stopped."
    else
    		echo "service has not been started."
    fi
}

check(){
   if exist; then
   	 echo "service is alive."
   	 exit 0
   else
   	 echo "service is dead."
   	 exit -1
   fi
}

restart(){
        stop
        start
}

case "$1" in

start)
        start
;;
stop)
        stop
;;
restart)
        restart
;;
check)
        check
;;
*)
        echo "available operations: [start|stop|restart|check]"
        exit 1
;;
esac