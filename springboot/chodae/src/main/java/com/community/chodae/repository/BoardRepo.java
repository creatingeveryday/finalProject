package com.community.chodae.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.chodae.domain.Board;

public interface BoardRepo  extends JpaRepository<Board, Long> {

}
