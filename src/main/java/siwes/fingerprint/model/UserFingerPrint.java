package siwes.fingerprint.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Simon
 *
 */
@Entity
@Table(name = "user_finger_print")
public class UserFingerPrint implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1589083633711580633L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "Id", nullable = false)
	private Integer id;
	
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	
	@Column(name = "left_thumb", nullable = true, length=1700)
	private byte[] leftThumb;
	
	@Column(name = "left_index_finger", nullable = true,length=2000)
	private byte[] leftIndex;
	
	public UserFingerPrint(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public byte[] getLeftThumb() {
		return leftThumb;
	}

	public void setLeftThumb(byte[] leftThumb) {
		this.leftThumb = leftThumb;
	}

	public byte[] getLeftIndex() {
		return leftIndex;
	}

	public void setLeftIndex(byte[] leftIndex) {
		this.leftIndex = leftIndex;
	}
	
	

}
