package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.FgInventory;
import generator.service.FgInventoryService;
import generator.mapper.FgInventoryMapper;
import org.springframework.stereotype.Service;

/**
* @author dingguozhao
* @description 针对表【fg_inventory(成品库存表)】的数据库操作Service实现
* @createDate 2023-08-31 13:32:07
*/
@Service
public class FgInventoryServiceImpl extends ServiceImpl<FgInventoryMapper, FgInventory>
    implements FgInventoryService{

}




