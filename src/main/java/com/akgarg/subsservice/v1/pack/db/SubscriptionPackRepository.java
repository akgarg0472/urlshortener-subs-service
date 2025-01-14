package com.akgarg.subsservice.v1.pack.db;

import com.akgarg.subsservice.v1.pack.SubscriptionPack;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface SubscriptionPackRepository extends JpaRepository<SubscriptionPack, String> {

    List<SubscriptionPack> findAllByVisibleAndDeleted(boolean visible, boolean deleted, PageRequest pageRequest);

    Optional<SubscriptionPack> findByDefaultPackTrue();

}
