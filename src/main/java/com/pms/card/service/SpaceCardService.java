package com.pms.card.service;

import com.pms.card.entity.CardRequest;
import com.pms.card.entity.SpaceCard;
import com.pms.card.repository.SpaceCardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 卡片服务：卡片的增删改查。
 */
@Service
public class SpaceCardService {

    private final SpaceCardRepository repository;

    public SpaceCardService(SpaceCardRepository repository) {
        this.repository = repository;
    }

    /** 查询单张卡片，不存在时返回 null。 */
    @Transactional(readOnly = true)
    public SpaceCard get(Long id) {
        return repository.findById(id).orElse(null);
    }

    /** 查询某个空间下的卡片列表，可按状态过滤。 */
    @Transactional(readOnly = true)
    public List<SpaceCard> list(Long spaceId, String status) {
        if (status == null || status.trim().isEmpty()) {
            return repository.findBySpaceIdOrderByIdDesc(spaceId);
        }
        return repository.findBySpaceIdAndStatusOrderByIdDesc(spaceId, status);
    }

    /** 新建卡片。 */
    @Transactional
    public SpaceCard create(CardRequest request) {
        SpaceCard card = new SpaceCard();
        card.setSpaceId(request.getSpaceId());
        card.setTitle(request.getTitle());
        card.setType(request.getType());
        card.setContent(request.getContent());
        card.setStatus(request.getStatus() == null ? "DRAFT" : request.getStatus());
        card.setCreatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());
        return repository.save(card);
    }

    /** 更新卡片。卡片不存在时返回 null。 */
    @Transactional
    public SpaceCard update(Long id, CardRequest request) {
        SpaceCard card = repository.findById(id).orElse(null);
        if (card == null) {
            return null;
        }
        card.setTitle(request.getTitle());
        card.setType(request.getType());
        card.setContent(request.getContent());
        if (request.getStatus() != null) {
            card.setStatus(request.getStatus());
        }
        card.setUpdatedAt(LocalDateTime.now());
        return repository.save(card);
    }

    /** 删除卡片。返回是否删除成功。 */
    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
