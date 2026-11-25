package com.xs.menu;

public enum LogType {


        QDREQ("》》》渠道上送请求","01"),
        QDRES("《《《返回渠道","02"),
        F3REQ("》》对第三方发送请求","03"),
        F3RES("《《接收第三方返回","04"),

        ZH_SERVE("转换报文主程序","05"),
        ZH_QT("转换报文辅助程序","06"),

        TIME("计算时间","07"),

        JXBQ("解析标签","08");


        //成员变量
        private String name;
        private String index;

        //构造方法
        private LogType(String name, String index)
        {
            this.name=name;
            this.index=index;
        }

        public String getIndex () {
            return this.index;
        }

        public String getName () {
                return this.name;
        }
        //覆盖方法
        @Override
        public String toString()
        {
            return this.index+"-"+this.name;
        }


}