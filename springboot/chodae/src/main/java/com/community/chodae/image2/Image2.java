package com.community.chodae.image2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import com.community.chodae.domain.Post;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = "post")
@Entity
@Table(name = "image2")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Image2 {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "filename")
	private String filename;
	
	@Column(name = "fileDownloadUri")
	private String fileDownloadUri;
	
	@Column(name = "fileType")
	private String fileType;
	
	@Column(name = "size")
	private long size;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "post_no")
	private Post post;
	
}
