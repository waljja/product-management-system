package com.ktg.mes.fg.service.impl;

import com.ktg.mes.fg.domain.FgInventory;
import com.ktg.mes.fg.domain.FgToList;
import com.ktg.mes.fg.domain.FgTos;
import com.ktg.mes.fg.mapper.FgInventoryMapper;
import com.ktg.mes.fg.mapper.FgTolistMapper;
import com.ktg.mes.fg.mapper.FgTosMapper;
import com.ktg.mes.fg.service.FgTolistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 成品备货清单Service业务层处理
 *
 * @author JiangTingming
 * @date 2023-05-05
 */
@Service
public class FgTolistServiceImpl implements FgTolistService {
    @Autowired
    private FgTolistMapper fgTolistMapper;

    @Autowired
    private FgInventoryMapper fgInventoryMapper;

    @Autowired
    private FgTosMapper fgTosMapper;

    @Override
    public long getReadyBH() {
        return fgTolistMapper.getReadyBH();
    }

    @Override
    public long getBHing() {
        return fgTolistMapper.getBHing();
    }

    /**
     * 查询TO明细
     *
     * @param id TO明细主键
     * @return TO明细
     */
    @Override
    public FgToList selectFgTolistById(Long id)
    {
        return fgTolistMapper.selectFgTolistById(id);
    }

    /**
     * 查询TO明细列表
     *
     * @param fgTolist TO明细
     * @return TO明细
     */
    @Override
    public List<FgToList> selectFgTolistList(FgToList fgTolist)
    {
        if (fgTolist.getStatus() != null && fgTolist.getStatus() == -1) {
            return fgTolistMapper.getAreaStockInfo(fgTolist);
        } else {
            return fgTolistMapper.selectFgTolistList(fgTolist);
        }

    }

    @Override
    public List<FgToList> selectFgTolistList2(FgToList fgToList) {
        return fgTolistMapper.selectFgTolistList2(fgToList);
    }

    /**
     * 新增TO明细
     *
     * @param fgTolist TO明细
     * @return 结果
     */
    @Override
    public int insertFgTolist(FgToList fgTolist)
    {
        return fgTolistMapper.insertFgTolist(fgTolist);
    }

    /**
     * 修改TO明细
     *
     * @param fgTolist TO明细
     * @return 结果
     */
    @Override
    public int updateFgTolist(FgToList fgTolist)
    {
        return fgTolistMapper.updateFgTolist(fgTolist);
    }

    /**
     * 批量删除TO明细
     *
     * @param ids 需要删除的TO明细主键
     * @return 结果
     */
    @Override
    public int deleteFgTolistByIds(Long[] ids)
    {
        return fgTolistMapper.deleteFgTolistByIds(ids);
    }

    /**
     * 删除TO明细信息
     *
     * @param id TO明细主键
     * @return 结果
     */
    @Override
    public int deleteFgTolistById(Long id)
    {
        return fgTolistMapper.deleteFgTolistById(id);
    }

    /**
     * 上传产生TO单
     * @param shipmentNO 走货单
     * @param carNO 车号
     * @param batch 650对应660的批次
     * @param pn 660PN (检查是否有欠货单)
     * @param po 660PO
     * @param quantity 660对应数量
     * */
    @Override
    public String uploadCK00(String shipmentNO, String carNO, String batch, String pn, String po, long quantity) {

        Date date = new Date();
        List<FgInventory> fgInventoryList = fgInventoryMapper.getCK00Info(batch);
        if (fgInventoryList.size() == 0) {
            System.out.println("上传批次" + batch + "信息不需要预留，请检查是否已预留或重复上传");
            return "上传批次" + batch + "信息不需要预留，请检查是否已预留或重复上传";
        }
        // 查询是否已存在备货单/欠货单
        String BH = fgTosMapper.checkTosByshipmentno_BH(shipmentNO) == null ? "" : fgTosMapper.checkTosByshipmentno_BH(shipmentNO.toString());
        String QH = fgTosMapper.checkTosByshipmentno_QH(shipmentNO) == null ? "" : fgTosMapper.checkTosByshipmentno_QH(shipmentNO.toString());
        for (int i = 0;i < fgInventoryList.size();i++) {

            FgTos fgTos = new FgTos();
            FgToList fgToList = new FgToList();
            if ("".equals(BH)) {
                BH = generateTo_No("备货单");
                fgTos.setTo_No(BH);
                fgTos.setShipmentNO(shipmentNO);
                fgTos.setSap_qty(fgInventoryList.get(i).getQuantity());
                fgTos.setCarNo(carNO);
                fgTos.setPlant(String.valueOf(fgInventoryList.get(i).getPlant()));
                fgTos.setStatus(0);
                fgTos.setCreateTime(date);
                // 插入TO管理
                fgTosMapper.insertFgTos(fgTos);
            }
            fgToList.setTo_No(BH);
            fgToList.setUid(fgInventoryList.get(i).getUid());
            fgToList.setPn(fgInventoryList.get(i).getPn650());
            fgToList.setPo(po);
            fgToList.setQuantity(fgInventoryList.get(i).getQuantity());
            fgToList.setStatus(0);
            fgToList.setStock(fgInventoryList.get(i).getStock());
            fgToList.setBatch(fgInventoryList.get(i).getBatch());
            fgToList.setCreateTime(date);
            fgTosMapper.insertFgTolist(fgToList);
            // 库存状态为 已预留
            fgInventoryMapper.updateStatusByUid(fgInventoryList.get(i).getUid());
            // 更新TOs总数
            long toNoSum = fgTosMapper.gettoNoSum(BH);
            fgTosMapper.updateTosQuantity(toNoSum, BH);
            fgTosMapper.updateTolistQuantity(toNoSum, BH);

        }
        if (!"".equals(QH) && fgInventoryMapper.checkPN(pn, QH, po) == 1) {
            // 更新该欠货单\PN、PO欠货数量
            int n = fgInventoryMapper.updateQHQuantity(QH, pn, po, quantity);
            if (n <= 0) {
                System.out.println(pn + "更新欠货单数量失败");
                return pn + "更新欠货单数量失败";
            }
            // 数量为0即补货完
            if (fgInventoryMapper.checkQHQuantity(QH, pn, po, quantity) == 0) {
                // 更新650对应的660为补货状态
                fgInventoryMapper.updateQHStatus(QH, pn, po, quantity);
            }
            // 查询该欠货单还剩多少欠货
            long sumqty = fgTosMapper.getSumqty(shipmentNO);
            if (sumqty == 0) {
                fgTosMapper.updateTosQH(QH, shipmentNO);
            } else {
                fgTosMapper.updateQuantity(sumqty, shipmentNO);
                fgTosMapper.updateQuantity2(sumqty, QH);
            }
        }

        return "TO已生成";
    }

    /**
     * 根据 BH或QH + 年、月、日、时、分、秒、毫秒（三位数） 生成备货单/备货单
     *
     * @param tono 描述是备货单还是欠货单
     * @return String
     **/
    public String generateTo_No(String tono) {

        String To_No = "";
        try {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            if (tono.equals("备货单")) {
                // BH + 年月日时分秒 毫秒
                To_No = "BH" + simpleDateFormat.format(date);
                System.out.println(To_No);
            } else if (tono.equals("欠货单")) {
                // QH + 年月日时分秒 毫秒
                To_No = "QH" + simpleDateFormat.format(date);
                System.out.println(To_No);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return To_No;

    }
}
