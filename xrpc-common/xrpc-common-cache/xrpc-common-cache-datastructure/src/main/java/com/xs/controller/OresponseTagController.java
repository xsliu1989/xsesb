package com.xs.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("oresponsetag")
@RequestMapping("oresponsetag")
public class OresponseTagController {
//        extends CrudController<OresponseTag> {
//
//    @Autowired
//    protected CrudBaseServiceImpl baseService;
//
//    @PostMapping("/getOresponseTags/{parentstr}")
//    public CResult getOresponseTags(@PathVariable String parentstr){
//
//
//        return new CResult(getByTag(parentstr, parentstr));
//    }
//
//    public List<OresponseTag> getByTag(String tag, String tranno){
//
//        OresponseTag oresponseTag = new OresponseTag();
//        oresponseTag.setParentTag(tag);
//
//        List<OresponseTag> oresponseTagList = baseService.findAll(oresponseTag);
//
//        if(oresponseTagList.size() > 0){
//            for(OresponseTag oresponseTag_ : oresponseTagList){
//                String parenttag_ = oresponseTag_.getParentTag();
//                String tag_ = oresponseTag_.getTag();
//                oresponseTag_.setChildren(tag.equals(tranno) ? this.getByTag(tag_, tranno) : this.getByTag(parenttag_+"."+tag_, tranno));
//            }
//        }
//
//        return oresponseTagList;
//
//    }
}
