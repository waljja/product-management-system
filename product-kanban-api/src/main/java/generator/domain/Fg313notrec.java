package generator.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName FG_313NotRec
 */
@TableName(value ="FG_313NotRec")
@Data
public class Fg313notrec implements Serializable {
    private Object id;

    private String partnumber;

    private String poNumber;

    private String batch;

    private String uid;

    private String status;

    private Double quantity;

    private Integer plant;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Fg313notrec other = (Fg313notrec) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPartnumber() == null ? other.getPartnumber() == null : this.getPartnumber().equals(other.getPartnumber()))
            && (this.getPoNumber() == null ? other.getPoNumber() == null : this.getPoNumber().equals(other.getPoNumber()))
            && (this.getBatch() == null ? other.getBatch() == null : this.getBatch().equals(other.getBatch()))
            && (this.getUid() == null ? other.getUid() == null : this.getUid().equals(other.getUid()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getPlant() == null ? other.getPlant() == null : this.getPlant().equals(other.getPlant()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPartnumber() == null) ? 0 : getPartnumber().hashCode());
        result = prime * result + ((getPoNumber() == null) ? 0 : getPoNumber().hashCode());
        result = prime * result + ((getBatch() == null) ? 0 : getBatch().hashCode());
        result = prime * result + ((getUid() == null) ? 0 : getUid().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        result = prime * result + ((getPlant() == null) ? 0 : getPlant().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", partnumber=").append(partnumber);
        sb.append(", poNumber=").append(poNumber);
        sb.append(", batch=").append(batch);
        sb.append(", uid=").append(uid);
        sb.append(", status=").append(status);
        sb.append(", quantity=").append(quantity);
        sb.append(", plant=").append(plant);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}