package com.xs.util;

import com.xs.exception.DefineException;
import com.xs.menu.LogType;
import com.xs.model.Tag;
import com.xs.serve.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 定长处理的util
 */
public class DCStringMapUtil {

    private final static Logger log = (Logger) LoggerFactory.getLogger(DCStringMapUtil.class);

    private  static LogUtil logUtil;

    public static HashMap dcStringMap(List<Tag> tags, String dcStr) throws DefineException{
        try {
            return get(tags, tags.get(0).getParentTag(), dcStr);
        } catch (DefineException e){
            throw e;
        } catch (Exception e){
            logUtil.error(log, e.getMessage(), e);
            throw new DefineException("6s532","定长报文解析错误，请检查上送格式");
        }
    }


    public static  HashMap get(List<Tag> tags, String parentStr, String dcStr) throws DefineException{
        try {
            // 取得源系统的请求TAG信息
            List<Tag> customerRequestTags = getChildTagsByParentstr(tags, parentStr);
            customerRequestTags.sort(Comparator.comparing(Tag::getSort));

            HashMap hashMap = new HashMap();

            // 索引
            int s = 0;
            // 该字段是提在循环list之前拿到list的个数；
            int listCount=0;
            for (Tag customerRequestTag : customerRequestTags) {
                logUtil.info(LogType.JXBQ, log, "解析标签："+ customerRequestTag.getTagName());

                if (customerRequestTag.getTagType().equals("String")) {
                    logUtil.info(LogType.JXBQ, log, "报文:"+ dcStr +" 开始："+ s + " 长度："+ customerRequestTag.getTaglength());
                    hashMap.put(customerRequestTag.getTagName(), dcStr.substring(s,  s + customerRequestTag.getTaglength()));

                    if(null != customerRequestTag.getTagflag() && customerRequestTag.getTagflag().equals("ListSizeCount")){
                        String size = dcStr.substring(s,  s + customerRequestTag.getTaglength()).trim();
                        listCount = Integer.parseInt(size);
                    }
                } else if (customerRequestTag.getTagType().equals("NoSizeList")) {
                    // NoSizeList： listStr无法计算，直接从起点获取到结束

                    String listStr = dcStr.substring(s);
                    ArrayList arrayList = new ArrayList();
                    // 一个list的长度
                    int st = 0;
                    int l = customerRequestTag.getTaglength();
                    while (listStr.length() > 0) {

                        String mess = "";
                        try {
                            mess = listStr.substring(st, st + l);
                        } catch (Exception e) {
                            break;
                        }


                        HashMap hm = get(tags, customerRequestTag.getTagName(), mess);
                        arrayList.add(hm);
                        st = st + l;

                    }


                    hashMap.put(customerRequestTag.getTagName(), arrayList);

                } else if (customerRequestTag.getTagType().equals("SizeList")) {
                    // SizeList: listStr需计算
                    ArrayList arrayList = new ArrayList();
                    // 有两种形式，第一种是知道了大小
                    //           第二种是不知道大小，一直读到最后，这样的就需要解析。

                    int taglen = customerRequestTag.getTaglength();

                    for(int i=0; i<listCount; i++){
                        HashMap hm = get(tags, customerRequestTag.getParentTag() +"."+ customerRequestTag.getTagName(), dcStr.substring(s, s+ taglen));
                        arrayList.add(hm);


                        s = s + taglen;
                    }
                    hashMap.put(customerRequestTag.getTagName(), arrayList);

                }

                if (customerRequestTag.getTaglength() == null) {

                } else {
                    s = s + customerRequestTag.getTaglength();
                }

            }
            return hashMap;
        } catch (DefineException e){
            throw e;
        } catch (Exception e){
            logUtil.error(log, "定长报文解析错误，请检查上送格式"+e.getMessage(), e);
            throw new DefineException("6532","定长报文解析错误，请检查上送格式");
        }
    }


    /**
     * 通过 parent 找到子标签，
     * @param parentStr
     * @return
     */
    public static   List<Tag> getChildTagsByParentstr(List<Tag> tags, String parentStr) {


        List<Tag> childTagList = tags.stream().filter(tag -> Objects.equals(tag.getParentTag(),parentStr)).collect(Collectors.toList());
        return childTagList;
    }
}
