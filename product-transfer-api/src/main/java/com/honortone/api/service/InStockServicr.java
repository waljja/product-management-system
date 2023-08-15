package com.honortone.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.honortone.api.controller.dto.InventoryDto;
import com.honortone.api.controller.dto.MaterialTransationsDto;
import com.honortone.api.entity.Inventory;
import com.honortone.api.entity.MaterialTransactions;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface InStockServicr extends IService<Inventory> {

    public String InStock(MaterialTransationsDto materialTransationsDto);

    // public String ifSuccess(String uid);

    public MaterialTransationsDto ifSuccess(String uid, String stock);

    String inAndUpdateStatus(String uid, String fromstock, String tostock, String username);


}
