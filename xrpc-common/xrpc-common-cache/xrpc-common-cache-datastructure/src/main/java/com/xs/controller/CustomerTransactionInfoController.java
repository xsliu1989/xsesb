package com.xs.controller;

import com.xs.cache.DataStructureCache;
import com.xs.controller.bean.CResult;
import com.xs.datamodel.customer.CustomerTransactionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController("custTraninfoController")
@RequestMapping("custTraninfoController")
public class CustomerTransactionInfoController extends CrudBaseController<CustomerTransactionInfo> {


    @Autowired
    DataStructureCache dataStructureCache;

    @PostMapping("/uploadDataStruct")
    public CResult addEntity() {

        dataStructureCache.init();

        return new CResult();
    }



    }
