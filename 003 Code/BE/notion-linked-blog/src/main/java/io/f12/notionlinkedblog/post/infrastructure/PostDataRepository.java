package io.f12.notionlinkedblog.post.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.hashtag.serivce.port.PostHashtagRepository;
import io.f12.notionlinkedblog.post.service.port.PostRepository;

@Repository
public interface PostDataRepository extends JpaRepository<PostEntity, Long>, PostRepository, PostHashtagRepository {

	@Override
	@Query("SELECT p "
		+ "FROM PostEntity p join fetch p.user left join fetch p.likes "
		+ "WHERE p.id = :id")
	Optional<PostEntity> findById(@Param("id") Long id);

	@Override
	@Query("SELECT DISTINCT p FROM PostEntity p left join fetch p.likes")
	List<PostEntity> findByPostIdForTrend();

	@Override
	@Query("SELECT DISTINCT p.thumbnailName FROM PostEntity p WHERE p.thumbnailName  = :thumbnailName")
	String findThumbnailWithName(@Param("thumbnailName") String name);

	@Override
	@Query("SELECT p FROM PostEntity p LEFT JOIN FETCH p.hashtag WHERE p.id = :postId")
	List<PostEntity> findByIdForHashtag(@Param("postId") Long id);
}
