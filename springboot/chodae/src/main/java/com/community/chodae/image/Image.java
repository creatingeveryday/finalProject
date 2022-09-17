package com.community.chodae.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

import com.community.chodae.domain.Post;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@ToString(exclude = "post")
@Entity
@Table(name = "image")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Image {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "type")
	private String type;

	@Column(name = "image", unique = false, nullable = false, length = 100000)
	private byte[] image;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "post_no")
	private Post post;
	
}