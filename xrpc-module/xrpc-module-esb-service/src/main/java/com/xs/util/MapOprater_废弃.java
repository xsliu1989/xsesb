package com.xs.util;

public class MapOprater_废弃 {

//    /**
//     * list取值用，无需循环
//     * @param map
//     * @param key
//     * @return
//     * @throws Exception
//     */
//    public static Object getVal(HashMap map, String key, String defaluVal) throws Exception {
//        if (key == null){
//            return defaluVal;
//        }
//            String[] keys = key.split("\\.");
//            Object obj = (Object) map.get(keys[keys.length-1]);
//            if(obj instanceof String){
//                return obj.toString();
//            } else if (obj instanceof Integer){
//                return obj.toString();
//            }  else if (obj instanceof List){
////                return obj;
//            } else {
//                for (int i = 0; i<keys.length; i++ ){
//                    obj = (Object) map.get(keys[i]);
//                    if (obj instanceof String){
//                        return obj;
//                    } else {
//                        if (obj instanceof HashMap){
//                            obj = com.alibaba.fastjson.JSONObject.toJSONString(obj);
//                            map = (HashMap) MapOprater.parseJSON2Map(obj.toString());
//                        }else {
//                            map = (HashMap) MapOprater.parseJSON2Map(obj.toString());
//                        }
//
//                    }
//                }
//            }
//        return null;
//    }

//    /**
//     * 如果不是list, 需要循环取出map,再从对应map里取值
//     * @param map
//     * @param key
//     * @param defaluVal
//     * @return
//     * @throws Exception
//     */
//    public static Object getVal1(HashMap map, String key, String defaluVal) throws Exception {
//        if (key == null){
//            return defaluVal;
//        }
//        String[] keys = key.split("\\.");
//        for (int i = 0; i < keys.length; i++) {
//            Object obj = (Object) map.get(keys[i]);
//            if(null == obj){
//                return "";
//            } else if(obj instanceof String && i == keys.length-1){
//                return obj == null || obj.toString().equals("")  ? defaluVal : obj.toString();
//            } else if (obj instanceof Integer && i == keys.length-1){
//                return obj.toString();
//            } else if (obj instanceof Long && i == keys.length-1){
//                return String.valueOf(obj);
//            }  else if (obj instanceof BigInteger && i == keys.length-1){
//                return ((BigInteger) obj).toString();
//            } else if( obj instanceof Float  && i == keys.length-1){
//                return Float.toString((Float) obj);
//            } else if( obj instanceof Double  && i == keys.length-1){
//                return Double.toString((Double)obj);
//            } else if( obj instanceof BigDecimal && i == keys.length-1){
//                return obj.toString();
//            } else if (obj instanceof List ){
//                return obj;
//            } else if (obj instanceof HashMap && i == keys.length-1 && keys[keys.length-1].equals("v")){
//                ArrayList list = new ArrayList();
//                list.add(obj);
//                return list;
//            } else if (obj instanceof HashMap){
////                        obj = com.alibaba.fastjson.JSONObject.toJSONString(obj);
////                        map = (HashMap) MapOprater.parseJSON2Map(obj.toString());
//                map = (HashMap) obj;
//            } else {
////                    throw  new DefineException("7999", "MapOprater未使用的标签");
////                    map = (HashMap) MapOprater.parseJSON2Map(obj.toString());
//                try {
//                    return obj.toString();
//                } catch (Exception e){
//                    e.printStackTrace();
//                    return e.getMessage();
//                }
//            }
//
//        }
//        return null;
//    }
////    public static Object getValList(List list, String key) throws Exception {
////        String[] keys = key.split("\\.");
////        return null;
////    }
//
////    //String转map
////    public static Map<String, Object> parseJSON2Map(String jsonStr) {
////            Map<String, Object> map = new HashMap<String, Object>();
////            JSONObject json = JSONObject.fromObject(jsonStr);
////            for (Object k : json.keySet()) {
////                Object v = json.get(k);
////                if (v instanceof List) {
////                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
////                    Iterator it = ((JSONArray) v).iterator();
////                    while (it.hasNext()) {
////                        Object json2 = it.next();
////                        list.add(parseJSON2Map(json2.toString()));
////                    }
////                    map.put(k.toString(), list);
////                } else {
////                    map.put(k.toString(), v);
////                }
////            }
////        return map;
////    }
//
//    /**
//     * 根据映射转换成map   TrequestTag
//     * @param dsftranId  接口号，用来判断顶级标签
//     * @param hm 相当于数据源， Tags里的数据，都是从hm里根据规则拿出来的
//     * @param tags 标签list,转完之后的结构信息。  从数据库里拿出来。
//     */
//    public static HashMap<String,Object> mapperMap(String dsftranId, HashMap hm, List<Tag> tags) throws Exception {
//
//        //
//        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println("MapperMap方法开始时间"+ dateformat.format(System.currentTimeMillis()));;
//        // 根据trequesttags规则，从hm数据源拿值， 最后整合到resultmap
//        HashMap<String, Object> resultMap = new HashMap<String, Object>();
//
//        int size = tags.size();
//
//        for(int i=0; i<size; i++) {
//            Tag tag = tags.get(i);
//
//            // 获得上级标签的名称，如果为空，则为顶级标签
//            String tPName = tag.getParentTag();
//            // 判断当前TAG是否是顶级标签
//            if (dsftranId.equals(tPName)) {  // 是顶级标签
//                if (tag.getTagType().equals("Object")) {
//                    HashMap<String, Object> item  = new HashMap<String, Object>();
//                    resultMap.put(tag.getTagName(), item);
//                }  else if(tag.getTagType().equals("List")){
//                    ArrayList list= (ArrayList) hm.get(tag.getTagName());
//                    resultMap.put(tag.getTagName(), listHandle(tag.getTagName(),list,tags));
//                } else {
//                    resultMap.put(tag.getTagName(), MapOprater.getVal1(hm, tag.getTagMapping() , tag.getTagValue()));
//                }
//            } else if (FindByKey(resultMap,tPName)!=null && FindByKey(resultMap,tPName) instanceof HashMap){               // 不是顶级标签
//                // 根据上级标签的层级进行循环,找到上级的hashmap
////                String[] tPNames = tPName.split("\\.");
//                HashMap item = (HashMap<String, Object>) FindByKey(resultMap,tPName);
//                // 向找到的hashmap里加值
//                if (tag.getTagType().equals("Object")) {
//                    item.put(tag.getTagName(), new HashMap<String, Object>());
//                }  else if(tag.getTagType().equals("List")){
//                    ArrayList list= (ArrayList)MapOprater.getVal1(hm, tag.getTagMapping() , tag.getTagValue());
//                    item.put(tag.getTagName(), listHandle(tag.getParentTag()+"."+tag.getTagName(),list,tags));
//                }  else if (tag.getTagType().equals("Array")) {
//                    Object array= (Object)MapOprater.getVal1(hm, tag.getTagMapping() , tag.getTagValue());
//                    item.put(tag.getTagName(), array);
//                }  else if(tag.getTagType().equals("String") && null != tag.getTagflag() &&  tag.getTagflag().equals("listsize")){   // listsize计算list的大小
//                    ArrayList list= (ArrayList)MapOprater.getVal1(hm, tag.getTagMapping() , tag.getTagValue());
//                    item.put(tag.getTagName(), list.size());
//                }  else if (tag.getTagType().equals("String")) {
//                    try {
//                        System.out.printf("tag.getTname:"+ tag.getTagName());
//                        Object val = MapOprater.getVal1(hm, tag.getTagMapping() , tag.getTagValue());
//                        item.put(tag.getTagName(), null == val ? "" : val);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    item.put(tag.getTagName(), MapOprater.getVal1(hm, tag.getTagMapping(), tag.getTagValue()));
//                }
//            }
//        }
////        String jsonStr = JSON.toJSONString( resultMap );
////        System.out.println("转换之后的json串:"+ jsonStr +"结束");
//        System.out.println("MapperMap方法结束时间"+ dateformat.format(System.currentTimeMillis()));;
//        return resultMap;
//
//    }
//
//    public static List listHandle(String dsftranId, ArrayList hm, List<Tag> tags) throws Exception {
//        //
//        List list = new ArrayList();
//        HashMap<String, Object> resultMap = new HashMap<String, Object>();
//        for (int j = 0;j<hm.size();j++){
//            resultMap = new HashMap<>();
//            HashMap result = (HashMap) hm.get(j);
////            result.putAll(hashMap);
//            int size = tags.size();
//            for(int i=0; i<size; i++) {
//                Tag tag = tags.get(i);
//                // 获得上级标签的名称，如果为空，则为顶级标签
//                String tPName = tag.getParentTag();
//                // 判断当前TAG是否是顶级标签
//                if (dsftranId.equals(tPName)) {  // 是顶级标签
//                    if (tag.getTagType().equals("Object")) {
//                        HashMap<String, Object> item  = new HashMap<String, Object>();
//                        resultMap.put(tag.getTagName(), item);
//                    }  else if(tag.getTagType().equals("List")){
//                        ArrayList listResult = (ArrayList)result.get(tag.getTagName());
//                        resultMap.put(tag.getTagName(), listHandle(tag.getTagName(),listResult,tags));
//                    } else {
//                        resultMap.put(tag.getTagName(), getVal1(result, tag.getTagMapping(), tag.getTagValue()));
//                    }
//                }
//            }
//            list.add(resultMap);
//        }
////        String jsonStr = JSON.toJSONString( resultMap );
////        System.out.println("转换之后的json串:"+ jsonStr +"结束");
//        return list;
//    }
//
//
//    /**
//     * 通过key找到map里的项
//     * 也就是根据parent标签，找到该往那个map里新加值
//     * @param map
//     * @param key
//     * @return
//     */
//    public static Object FindByKey(Map map, String key){
//        int count = 0;     //  如果没有循环到最后，就返回null, 因为list标签下的子标签，不用在循环
//        String[] keys = key.split("\\.");
//        for (int i = 0; i < keys.length; i++) {
//            Object obj = (Object) map.get(keys[i]);
//
//            if(obj instanceof  Map){
//                map = (Map) obj;
//                count = count + 1;
//            }
//
//        }
//        if (count!=keys.length){
//            return null;
//        }else  {
//            return map;
//        }
//
////        if(map.get(key) != null){
////            return map.get(key);
////        }
////
////
////        for(Object Value:map.values()){
////            if(Value instanceof  Map){
////                Map map1 = (Map)Value;
////                Object Value1 = FindByKey(map1,key);
////                if(Value1 != null){
////                    return Value1;
////                }
////            }
////        }
////        return null;
//    }
}
