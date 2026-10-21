package com.xone.tenant.card.repository;

import com.xone.tenant.card.entity.SpaceCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 卡片仓储。
 */
@Repository
public interface SpaceCardRepository extends JpaRepository<SpaceCard, Long> {

    /** 按空间查询卡片列表。 */
    List<SpaceCard> findBySpaceIdOrderByIdDesc(Long spaceId);

    /** 按空间与状态查询卡片列表。 */
    List<SpaceCard> findBySpaceIdAndStatusOrderByIdDesc(Long spaceId, String status);
}
