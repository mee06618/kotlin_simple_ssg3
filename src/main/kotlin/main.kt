import java.text.SimpleDateFormat
import java.util.*

fun main() {
    println("== SIMPLE SSG 시작 ==")

    ArticleRepository.makeTestArticles()
    BoardRepository.addBoard()
    var login = 0
    var promt=""
    val memberController = MemberController
    val systemController = SystemController
    val articleController = ArticleController()
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

// 게시물 관련 시작



// 게시물 관련 끝




// 유틸 관련 시작
fun readLineTrim() = readLine()!!.trim()

object Util {
    fun getNowDateStr(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return format.format(System.currentTimeMillis())
    }
}




// 유틸 관련 끝