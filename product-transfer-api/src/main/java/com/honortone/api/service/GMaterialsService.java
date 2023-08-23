package com.honortone.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.honortone.api.entity.Inventory;
import com.honortone.api.entity.ShipmentInfoByhand;
import com.honortone.api.entity.TagsInventory;
import com.honortone.api.entity.ToList;
import org.apache.ibatis.annotations.Param;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface GMaterialsService extends IService<Inventory> {

    String checkQuantity(String uid);

    List<String> downloadCarno(String date);

    List<String> downloadShipmentno(String carno, String date);

    List<String> downloadTos(String shipmentno);

    List<String> downloadTos2(String shipmentno);

    List<ToList> downloadTono(String tono);

    List<TagsInventory> selectClientTag(String clientPn);

    ToList checkTolistUID(String uid);

    long getSumQuantity(String uid);

    int updateTagsStauts(String clientPn, long quantity);

    List<ToList> downloadOrder();

    String soldOut(String cpno, String role);

    List<ShipmentInfoByhand> downloadShipmentinfoByhand();

    List<ShipmentInfoByhand> getShipmentInfo(String client);

    int updateByid(int id);

    int updateAllByid(String client);

    List<Map<Integer, Integer>> getQty(String tono);

    List<String> downloadShipmentNo2(String date);

    List<Map<Integer, Integer>> getQuantity(String client);

    String updateToNo(String toNo) throws MessagingException, IOException;

}
