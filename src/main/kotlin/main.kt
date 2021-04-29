import java.text.SimpleDateFormat
import java.util.*

fun main() {
    println("== SIMPLE SSG 시작 ==")

    articleRepository.makeTestArticles()
    BoardRepository.addBoard()
    var login = 0
    var promt=""
    val memberController = MemberController
    val systemController = SystemController
    val articleController = ArticleController
    val boardController=BoardController
    while (true) {
        if(login==0)
            print("명령어) ")
        else
            print("${MemberRepository.getNick(login)}) ")
        val command = readLineTrim()

        val rq = Rq(command)

        when (rq.actionPath) {
            "/member/join"->{

                memberController.join()
            }
            "/member/login"->{
                login=memberController.login()
            }
            "/member/logout"->{
                login=memberController.logout()
            }

            "/system/exit" -> {
                systemController.exit()
                break
            }
            "/article/write" -> {
                articleController.wrtie(login)

            }
            "/article/list" -> {
                articleController.list(rq)
            }
            "/article/detail" -> {
                articleController.detail(rq,login)
            
            }
            "/article/modify" -> {
                articleController.modify(rq,login)

            }
            
            
            "/article/delete" -> {
                articleController.delete(rq,login)

            }
            "/board/add"->{
                boardController.write(login)
            }
            "/board/list"->{
                boardController.list()
            }
        }
    }

    println("== SIMPLE SSG 끝 ==")
}

// Rq는 UserRequest의 줄임말이다.
// Request 라고 하지 않은 이유는, 이미 선점되어 있는 클래스명 이기 때문이다.
class Rq(command: String) {
    // 데이터 예시
    // 전체 URL : /artile/detail?id=1
    // actionPath : /artile/detail
    val actionPath: String

    // 데이터 예시
    // 전체 URL : /artile/detail?id=1&title=안녕
    // paramMap : {id:"1", title:"안녕"}
    private val paramMap: Map<String, String>

    // 객체 생성시 들어온 command 를 ?를 기준으로 나눈 후 추가 연산을 통해 actionPath와 paramMap의 초기화한다.
    // init은 객체 생성시 자동으로 딱 1번 실행된다.
    init {
        // ?를 기준으로 둘로 나눈다.
        val commandBits = command.split("?", limit = 2)

        // 앞부분은 actionPath
        actionPath = commandBits[0].trim()

        // 뒷부분이 있다면
        val queryStr = if (commandBits.lastIndex == 1 && commandBits[1].isNotEmpty()) {
            commandBits[1].trim()
        } else {
            ""
        }

        paramMap = if (queryStr.isEmpty()) {
            mapOf()
        } else {
            val paramMapTemp = mutableMapOf<String, String>()

            val queryStrBits = queryStr.split("&")

            for (queryStrBit in queryStrBits) {
                val queryStrBitBits = queryStrBit.split("=", limit = 2)
                val paramName = queryStrBitBits[0]
                val paramValue = if (queryStrBitBits.lastIndex == 1 && queryStrBitBits[1].isNotEmpty()) {
                    queryStrBitBits[1].trim()
                } else {
                    ""
                }

                if (paramValue.isNotEmpty()) {
                    paramMapTemp[paramName] = paramValue
                }
            }

            paramMapTemp.toMap()
        }
    }

    fun getStringParam(name: String, default: String): String {
        return paramMap[name] ?: default
    }

    fun getIntParam(name: String, default: Int): Int {
        return if (paramMap[name] != null) {
            try {
                paramMap[name]!!.toInt()
            } catch (e: NumberFormatException) {
                default
            }
        } else {
            default
        }
    }


}
//컨트롤러
object MemberController {
    fun join() {
        MemberRepository.joinMember()
    }
    fun logout(): Int {
        println("로그아웃 됬습니다")
        return 0
    }
    fun login(): Int {
        return MemberRepository.loginMember()
    }
}
object ArticleController {
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
object BoardController{
    fun write(login:Int){

        BoardRepository.writeBoard(login)
    }
    fun list(){
        val boards = BoardRepository.listBoard()


        for (board in boards) {

            println("${board.num}, ${board.boardName}, ${board.boardId}")
        }
    }
}

object SystemController {
    fun exit() {
        println("프로그램을 종료합니다")
    }

}




// 게시물 관련 시작
data class Article(
    val id: Int,
    val regDate: String,
    var updateDate: String,
    var title: String,
    var body: String,
    var memberId:Int=0,
    var boardId:Int

)

object articleRepository {
    private val articles = mutableListOf<Article>()
    private var lastId = 0

    fun deleteArticle(article: Article) {
        articles.remove(article)
    }

    fun getArticleById(id: Int): Article? {
        for (article in articles) {
            if (article.id == id) {
                return article
            }
        }

        return null
    }


    fun addArticle(title: String, body: String) {
        val id = ++lastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()
        val random = Random()
        articles.add(Article(id, regDate, updateDate, title, body,1,random.nextInt(2)+1))
    }
    fun makeTestArticles() {
        for (id in 1..100) {
            addArticle("제목_$id", "내용_$id")
        }
    }

    fun modifyArticle(id: Int, title: String, body: String) {
        val article = getArticleById(id)!!

        article.title = title
        article.body = body
        article.updateDate = Util.getNowDateStr()
    }

    fun getFilteredArticles(searchKeyword: String, page: Int, itemsCountInAPage: Int,boardId:Int): List<Article> {
        val filtered1Articles = getSearchKeywordFilteredArticles(articles, searchKeyword,boardId)
        val filtered2Articles = getPageFilteredArticles(filtered1Articles, page, itemsCountInAPage)


        return filtered2Articles
    }

    private fun getSearchKeywordFilteredArticles(articles: List<Article>, searchKeyword: String,boardId:Int): List<Article> {
        val filteredArticles = mutableListOf<Article>()

        for (article in articles) {
            if ( article.boardId==boardId) {  //article.title.contains(searchKeyword) ||
                filteredArticles.add(article)
            }
        }

        return filteredArticles
    }
    private fun getIdFilteredArticles(boardId: String){

    }
    private fun getPageFilteredArticles(articles: List<Article>, page: Int, itemsCountInAPage: Int): List<Article> {
        val filteredArticles = mutableListOf<Article>()

        val offsetCount = (page - 1) * itemsCountInAPage

        val startIndex = articles.lastIndex - offsetCount
        var endIndex = startIndex - (itemsCountInAPage - 1)

        if (endIndex < 0) {
            endIndex = 0
        }

        for (i in startIndex downTo endIndex) {
            filteredArticles.add(articles[i])
        }

        return filteredArticles
    }

    fun writeArticle(login: Int) {

        if (login != 0) {
            print("제목 : ")
            val title = readLineTrim()
            print("내용 : ")
            val body = readLineTrim()
            print("게시판 : ")
            val boardId= readLineTrim().toInt()
            val id = ++lastId
            val regDate = Util.getNowDateStr()
            val updateDate = Util.getNowDateStr()

            articles.add(Article(id, regDate, updateDate, title, body, login,boardId))
        } else {
            println("로그인 해주세요")
        }
    }




}
// 게시물 관련 끝

data class Member(
    val num:Int,
    val id:String,
    val pass:String,
    val name:String,
    val nickname:String,
    val phone:String,
    val email:String,

    )

object MemberRepository {
    private val members = mutableListOf<Member>()
    private var lastNum = 0

    fun joinMember(){
        val num= ++lastNum
        print("로그인 아이디 : ")
        val id = readLineTrim()
        print("로그인 비번 : ")
        val pass = readLineTrim()
        print("이름 : ")
        val name= readLineTrim()
        print("별명 : ")
        val nickname = readLineTrim()
        print("휴대전화 번호 : ")
        val phone = readLineTrim()
        print("이메일 : ")
        val email = readLineTrim()
        for(member in members) {
            if (member.id==id) {
                println("${id}는 이미 존재합니다")
                return
            }
        }
        val temp = Member(num,id,pass,name,nickname,phone,email)
        members.add(temp)
        lastNum=num

        println("회원 가입이 완료되었습니다")
    }

    fun loginMember() :Int{
        print("아이디 : ")
        val id = readLineTrim()
        print("비밀번호 : ")
        val pass = readLineTrim()
        for(member in members) {
            if (member.id.contains(id)) {
                if(member.pass == pass){

                    println("${member.nickname}님 환영합니다!")

                    return member.num
                }else{
                    println("틀린 비번입니다")
                }
            } else {
                println("존재하지 않는 id입니다")
            }
        }
        return 0
    }
    fun getNick(num:Int):String{
        for(member in members) {
            if (member.num == num) {
                return member.nickname
            }
        }
        return ""
    }

}
data class Board(
    val num:Int,
    val boardName:String,
    val boardId: String
)
object BoardRepository {
    private val boards = mutableListOf<Board>()
    private var lastNum = 2
    fun addBoard() {


        boards.add(Board(1,"공지","notice"))
        boards.add(Board(2,"자유","free"))
    }
    fun writeBoard(login: Int) {
        val num=++lastNum
        if (login != 0) {
            print("게시판이름 : ")
            val name = readLineTrim()
            print("코드 : ")
            val boardid = readLineTrim()

            val regDate = Util.getNowDateStr()
            val updateDate = Util.getNowDateStr()
            if (boards.any { it.boardName == name } or boards.any { it.boardId == boardid }) {
                println("중복된 값입니다")
                return
            }

            boards.add(Board(lastNum,name, boardid))
            lastNum=num
        } else {
            println("로그인 해주세요")
        }

    }
    fun getName(id:Int):String {
        for (board in boards) {
            if (board.num==id) {
                return board.boardName
            }

        }
        return "미정"
    }
    fun listBoard():List<Board>{
        return boards
    }
}
// 유틸 관련 시작
fun readLineTrim() = readLine()!!.trim()

object Util {
    fun getNowDateStr(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return format.format(System.currentTimeMillis())
    }
}




// 유틸 관련 끝