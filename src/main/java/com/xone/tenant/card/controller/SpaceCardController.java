package com.xone.tenant.card.controller;

import com.xone.tenant.card.entity.CardRequest;
import com.xone.tenant.card.entity.SpaceCard;
import com.xone.tenant.card.service.SpaceCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 卡片接口：增删改查。
 */
@RestController
@RequestMapping("/api/cards")
public class SpaceCardController {

    private final SpaceCardService service;

    public SpaceCardController(SpaceCardService service) {
        this.service = service;
    }

    /** 查询单张卡片。 */
    @GetMapping("/{id}")
    public ResponseEntity<SpaceCard> detail(@PathVariable Long id) {
        SpaceCard card = service.get(id);
        return card == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(card);
    }

    /** 查询空间下的卡片列表。 */
    @GetMapping
    public List<SpaceCard> list(@RequestParam Long spaceId,
                                 @RequestParam(required = false) String status) {
        return service.list(spaceId, status);
    }

    /** 新建卡片。 */
    @PostMapping
    public ResponseEntity<SpaceCard> create(@Valid @RequestBody CardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    /** 更新卡片。 */
    @PutMapping("/{id}")
    public ResponseEntity<SpaceCard> update(@PathVariable Long id,
                                            @Valid @RequestBody CardRequest request) {
        SpaceCard card = service.update(id, request);
        return card == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(card);
    }

    /** 删除卡片。 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
