package com.community.chodae.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.chodae.domain.SnsInfo;

public interface SnsRepo extends JpaRepository<SnsInfo, String>{

}
