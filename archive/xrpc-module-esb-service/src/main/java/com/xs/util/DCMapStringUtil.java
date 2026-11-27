package com.xs.util;

import com.xs.exception.DefineException;
import com.xs.model.Tag;
import com.xs.serve.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 定长处理的util
 */
public class DCMapStringUtil {

    private final static Logger log = (Logger) LoggerFactory.getLogger(DCMapStringUtil.class);

    private  static LogUtil logUtil;

    public static String dcMapString(List<Tag> tags, HashMap map){
        try {
            return get(tags,tags.get(0).getParentTag(), map);
        } catch (DefineException e){
            throw e;
        } catch (Exception e){
            logUtil.error(log, e.getMessage(), e);
            throw new DefineException("6s532","定长报文解析错误，请检查上送格式");
        }
    }


    public static String get(List<Tag> tagss, String parentStr, HashMap map){
        try {
            // 取得源系统的请求TAG信息
            List<Tag> tags = getChildTagsByParentstr(tagss, parentStr);
            tags.sort(Comparator.comparing(Tag::getSort));

            StringBuffer sb = new StringBuffer();

            // 索引
            int s = 0;
            for (Tag tag : tags) {
                if (tag.getTagType().equals("String")) {
                    sb.append(String.format("%" + tag.getTaglength() + "s", MapUtil.getVal(map, tag.getTagMapping(), "")));

                } else if (tag.getTagType().equals("List")) {
                    List<HashMap> list = (List) MapUtil.getVal(map, tag.getTagMapping(), "");
                    for (int i = 0; i < list.size(); i++) {
                        String sblist = get(tagss, tag.getParentTag() + "." + tag.getTagName(), list.get(i));
                        sb.append(sblist);
                    }
                }

            }
            return sb.toString();
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
