package com.community.chodae.image2;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.community.chodae.domain.Category;
import com.community.chodae.domain.Post;
import com.community.chodae.domain.PostContent;
import com.community.chodae.image.postService;
import com.community.chodae.repository.CategoryRepo;
import com.community.chodae.repository.PostRepo;

import lombok.extern.java.Log;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

@Log
@RestController
public class ImageController {

	private final ImageRepository imageRepository;
	private final PostRepo postRepo;
	private final postService postService;

	   
	@Autowired
	public ImageController(ImageRepository imageRepository, PostRepo postRepo, postService postService, CategoryRepo categoryRepo) {
		
		this.imageRepository = imageRepository;
		this.postRepo = postRepo;
		this.postService = postService;
	
	}
   
	
	@GetMapping(path = {"/get/image/{filename}"})
	public ResponseEntity<Resource> downloadFile(@PathVariable String filename,HttpServletRequest request) throws MalformedURLException{
	
		Path filePath = Paths.get("/home/final/img/"+filename);

			Resource resource = new  UrlResource(filePath.toUri());
	
		
		return ResponseEntity.ok()
						.body(resource);
	
	
	 }
}
    
    
    
//}