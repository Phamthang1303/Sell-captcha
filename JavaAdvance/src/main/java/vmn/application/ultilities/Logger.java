//package vmn.application.ultilities;
//
//import org.apache.log4j.Level;
//import org.apache.log4j.PropertyConfigurator;
//
//public class Logger {
//    //
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package com.tblib.utilities;
//
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
//
//    public class MyLog {
//        String logPath;
//        private static org.apache.log4j.Logger logger = null;
//
//        public MyLog(String logFile) {
//            this.logPath = logFile;
//            PropertyConfigurator.configure(this.logPath);
//            logger = org.apache.log4j.Logger.getRootLogger();
//        }
//
//        public static void Debug(String logString) {
//            StackTraceElement ste = (new Throwable()).getStackTrace()[1];
//            String clsName = ste.getClassName();
//            clsName = clsName.substring(clsName.lastIndexOf(".") + 1, clsName.length());
//            if (!clsName.isEmpty()) {
//                org.apache.log4j.Logger var10000 = logger;
//                org.apache.log4j.Logger subLog = org.apache.log4j.Logger.getLogger(clsName);
//                if (subLog != null) {
//                    subLog.log(Level.DEBUG, " (" + ste.getFileName() + "," + ste.getLineNumber() + "," + ste.getMethodName() + " )" + logString);
//                }
//            }
//        }
//
//        public static void Infor(String logString) {
//            StackTraceElement ste = (new Throwable()).getStackTrace()[1];
//            String clsName = ste.getClassName();
//            clsName = clsName.substring(clsName.lastIndexOf(".") + 1, clsName.length());
//            if (!clsName.isEmpty()) {
//                org.apache.log4j.Logger var10000 = logger;
//                org.apache.log4j.Logger subLog = org.apache.log4j.Logger.getLogger(clsName);
//                if (subLog != null) {
//                    subLog.log(Level.INFO, " (" + ste.getFileName() + "," + ste.getLineNumber() + "," + ste.getMethodName() + " )" + logString);
//                }
//            }
//        }
//
//        public static void Error(String logString) {
//            StackTraceElement ste = (new Throwable()).getStackTrace()[1];
//            String clsName = ste.getClassName();
//            clsName = clsName.substring(clsName.lastIndexOf(".") + 1, clsName.length());
//            if (!clsName.isEmpty()) {
//                org.apache.log4j.Logger var10000 = logger;
//                org.apache.log4j.Logger subLog = org.apache.log4j.Logger.getLogger(clsName);
//                if (subLog != null) {
//                    subLog.log(Level.ERROR, " (" + ste.getFileName() + "," + ste.getLineNumber() + "," + ste.getMethodName() + " )" + logString);
//                }
//            }
//        }
//
//        public static void Fatal(String logString) {
//            StackTraceElement ste = (new Throwable()).getStackTrace()[1];
//            String clsName = ste.getClassName();
//            clsName = clsName.substring(clsName.lastIndexOf(".") + 1, clsName.length());
//            if (!clsName.isEmpty()) {
//                org.apache.log4j.Logger var10000 = logger;
//                org.apache.log4j.Logger subLog = org.apache.log4j.Logger.getLogger(clsName);
//                if (subLog != null) {
//                    subLog.log(Level.FATAL, " (" + ste.getFileName() + "," + ste.getLineNumber() + "," + ste.getMethodName() + " )" + logString);
//                }
//            }
//        }
//
//        public static void Warning(String logString) {
//            StackTraceElement ste = (new Throwable()).getStackTrace()[1];
//            String clsName = ste.getClassName();
//            clsName = clsName.substring(clsName.lastIndexOf(".") + 1, clsName.length());
//            if (!clsName.isEmpty()) {
//                org.apache.log4j.Logger var10000 = logger;
//                org.apache.log4j.Logger subLog = org.apache.log4j.Logger.getLogger(clsName);
//                if (subLog != null) {
//                    subLog.log(Level.WARN, " (" + ste.getFileName() + "," + ste.getLineNumber() + "," + ste.getMethodName() + " )" + logString);
//                }
//            }
//        }
//
//        public static void Error(Exception ex) {
//            Error(ex.getMessage());
//            StackTraceElement[] e = ex.getStackTrace();
//
//            for(int i = 0; i < e.length; ++i) {
//                Error(e[i].toString());
//            }
//
//        }
//    }
//
//}
