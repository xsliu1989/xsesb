package com.xs.util;

import com.xs.exception.DefineException;
import com.xs.model.Tag;
import com.xs.serve.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MapConvert {
    //日志log
    private final static Logger log =  LoggerFactory.getLogger(MapConvert.class);
    private  static LogUtil logUtil;

    private HashMap dataMap;
    private List<Tag> tags;


    /**
     * 根据映射转换成map   TrequestTag
     *
     * @param tranNo    接口号，用来判断顶级标签
     * @param datamap_   相当于数据源， Tags里的数据，都是从hm里根据规则拿出来的
     * @param tags      报文结构
     */
    public  HashMap<String, Object> mapperMap(String tranNo, HashMap datamap_, List<Tag> tags) throws DefineException {
        try {
            this.dataMap = datamap_;
            this.tags = tags;

            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            Tag xn = new Tag();
            xn.setParentTag(tranNo);
            xn.setTagflag("TOP"); // 说明要取第一级
            findChildTagToMapping(xn, resultMap);

//        HashMap<String, Object> resultMap_ = new HashMap<String, Object>();
//        resultMap_.put(topTags.get(0).getTagName(), resultMap);
            return resultMap;
        } catch (DefineException e){
            throw e;
        } catch (Exception e){
            logUtil.error(log, e.getMessage(), e);
            throw new DefineException("87342", "解析有误，请检查上送报文格式001");
        }
    }

    /**
     * 1. 通过 parent 找到子标签，
     * 2. 然后往hm里映射子标签和子标签对应的值。。
     * @
     * @param currentTag  当前节点的名字，  通过当前节点的名字，可以找到所有子标签
     * @param mappingMap 当前节点的map
     */
    public void findChildTagToMapping(Tag currentTag, HashMap<String, Object> mappingMap) throws DefineException {

        try {
            List<Tag> childTagList = getChildTagsByParentstr(currentTag);
            for (Tag tag: childTagList ) {
                tagMappingVal(mappingMap, tag, this.dataMap);
            }
        } catch (DefineException e){
            throw e;
        } catch (Exception e){
            logUtil.error(log, e.getMessage(), e);
            throw new DefineException("87342", "解析有误，请检查上送报文格式");
        }
    }

    /**
     * 通过 parent 找到子标签，
     * @param currentTag 当前TAG
     * @return
     */
    public  List<Tag> getChildTagsByParentstr(Tag currentTag)  throws DefineException{
        try {

            List<Tag> childTagList = null;
            if(null != currentTag.getTagflag() && currentTag.getTagflag().equals("TOP")) {  // TOP说明是第一层
                childTagList = tags.stream().filter(tag -> Objects.equals(tag.getParentTag(),currentTag.getParentTag())).collect(Collectors.toList());

            } else {
                childTagList = tags.stream().filter(tag -> Objects.equals(tag.getParentTag(), currentTag.getParentTag() + "." + currentTag.getTagName())).collect(Collectors.toList());

            }
            return childTagList;
        } catch (DefineException e){
            throw e;
        } catch (Exception e){
            logUtil.error(log, e.getMessage(), e);
            throw new DefineException("87342", "解析有误，请检查上送报文格式");
        }
    }

    /**
     * tag标签与相应的值对应到hm中
     * @param mappingMap    存放当前节点的map
     * @param tag
     * @return
     */
    public void tagMappingVal(HashMap mappingMap , Tag tag, HashMap dataHashMAP) throws DefineException{
        try {

            if(tag.getTagType().equals("Object")){
                HashMap<String,Object> hashMap = new HashMap<String, Object>();
                this.findChildTagToMapping(tag, hashMap);
                mappingMap.put(tag.getTagName(), hashMap);

            } else if(tag.getTagType().equals("String")) {
                mappingMap.put(tag.getTagName(), MapUtil.getVal(dataHashMAP, tag.getTagMapping(), tag.getTagValue()));

            } else if(tag.getTagType().equals("List") || tag.getTagType().equals("SizeList")) {
                // 先取出其数据源，因为数据源是List多个的，所以再根据TAG，分成多个。
                ArrayList list= (ArrayList) MapUtil.getVal(dataHashMAP, tag.getTagMapping() , tag.getTagValue());
                List<Tag> childTagList = getChildTagsByParentstr(tag);
                mappingMap.put(tag.getTagName(), listHandle( list, childTagList));

            }
        } catch (DefineException e){
            throw e;
        } catch (Exception e){
            logUtil.error(log, e.getMessage(), e);
            throw new DefineException("87342", "解析有误，请检查上送报文格式");
        }
    }


    /**
     * 把数据源中的值与tags相映射，放到list里
     * @param hm  数据源
     * @param tags tags
     * @return
     */
    public List listHandle(ArrayList hm, List<Tag> tags) throws DefineException {
       try {


            List list = new ArrayList();
            HashMap<String, Object> resultMap = new HashMap<String, Object>();
            for (int j = 0;j<hm.size();j++){
                resultMap = new HashMap<>();
                HashMap result = (HashMap) hm.get(j);
                int size = tags.size();
                for(int i=0; i<size; i++) {
                    Tag tag = tags.get(i);
                    tagMappingVal(resultMap, tag, result);
                }
                list.add(resultMap);
            }
            return list;
        } catch (DefineException e){
           throw e;
       } catch (Exception e){
           logUtil.error(log, e.getMessage(), e);
            throw new DefineException("87342", "解析有误，请检查上送报文格式");
       }
    }


}
