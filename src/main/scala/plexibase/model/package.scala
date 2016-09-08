package plexibase.model

import org.joda.time.{LocalDate, LocalDateTime}

case class ArticlePostView(name: String, content: String, createdOn: LocalDateTime)

case class Article(id: Long, name: String, content: String, createdOn: LocalDateTime)
