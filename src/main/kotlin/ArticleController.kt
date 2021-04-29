class ArticleController {
    val articleRepository =ArticleRepository
    fun wrtie(login: Int) {

        articleRepository.writeArticle(login)
    }

    fun list(rq: Rq) {
        val page = rq.getIntParam("page", 1)
        val searchKeyword = rq.getStringParam("searchKeyword", "")
        val boardId = rq.getIntParam("boardId",1)

        val filteredArticles = articleRepository.getFilteredArticles(searchKeyword, page, 10,boardId)

        println("번호 / 작성날짜  / 작성자 / 제목 / 게시판")

        for (article in filteredArticles) {
            val memberNick = MemberRepository.getNick(article.memberId)
            val boardName = BoardRepository.getName(article.boardId)
            println("${article.id} / ${article.regDate} /${memberNick}/ ${article.title} / ${boardName}")
        }
    }
    fun detail(rq: Rq,login:Int) {

        val id = rq.getIntParam("id", 0)

        if (id == 0) {
            println("id를 입력해주세요.")
            return
        }
        if(login!=0) {
            val article = articleRepository.getArticleById(id)
            if (article == null) {
                println("${id}번 게시물은 존재하지 않습니다.")
                return
            }
            println("번호 : ${article.id}")
            println("작성날짜 : ${article.regDate}")
            println("갱신날짜 : ${article.updateDate}")
            println("제목 : ${article.title}")
            println("내용 : ${article.body}")
        }else{
            println("로그인해주세요")
        }
    }

    fun modify(rq: Rq, login: Int) {
        if(login!=0){
            val id = rq.getIntParam("id", 0)

            if (id == 0) {
                println("id를 입력해주세요.")
                return
            }

            val article = articleRepository.getArticleById(id)

            if (article == null) {
                println("${id}번 게시물은 존재하지 않습니다.")
                return
            }

            print("${id}번 게시물 새 제목 : ")
            val title = readLineTrim()
            print("${id}번 게시물 새 내용 : ")
            val body = readLineTrim()

            articleRepository.modifyArticle(id, title, body)

            println("${id}번 게시물이 수정되었습니다.")
        }else{
            println("로그인해주세요")
        }
    }
    fun delete(rq: Rq, login: Int) {
        val id = rq.getIntParam("id", 0)

        if (id == 0) {
            println("id를 입력해주세요.")
            return
        }

        val article = articleRepository.getArticleById(id)

        if (article == null) {
            println("${id}번 게시물은 존재하지 않습니다.")
            return
        }
        println("${id}번 게시물이 삭제되었습니다.")
        articleRepository.deleteArticle(article)
    }
}