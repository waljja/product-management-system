package generator.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName fg_tos
 */
@TableName(value ="fg_tos")
@Data
public class FgTos implements Serializable {
    private Integer id;

    private String toNo;

    private Long status;

    private Double quantity;

    private String shipmentNo;

    private String carNo;

    private String plant;

    private String stock;

    private Date createdate;

    private String pn;

    private String po;

    private static final long serialVersionUID = 1L;
}