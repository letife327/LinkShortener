package az.texnoera.link_shortener.repository;

import az.texnoera.link_shortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UrlRepository extends JpaRepository<Url, Long> {
    public Url findByShortCode(String shortUrl);
}
