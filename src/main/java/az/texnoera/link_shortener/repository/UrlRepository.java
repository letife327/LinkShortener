package az.texnoera.link_shortener.repository;

import az.texnoera.link_shortener.entity.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, Long> {
    public Url findByShortCode(String shortUrl);
    @Query("SELECT l FROM Url l JOIN FETCH l.user")
    List<Url> findAllLinksWithUsers();
    List<Url> findByLastVisitTimeBefore(LocalDateTime dateTime);
    @Query("SELECT u FROM Url u WHERE u.user.id = :userId")
    Page<Url> findAllUrlsByUserId(Integer userId, Pageable pageable);

}
