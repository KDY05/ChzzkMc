# ChzzkMc
[![](https://jitpack.io/v/KDY05/ChzzkMc.svg)](https://jitpack.io/#KDY05/ChzzkMc)

ChzzkMc는 한국의 스트리밍 플랫폼인 치지직의 채팅을 마인크래프트 서버에 실시간으로 연동하고, 시청자들이 채팅으로 투표에 참여할 수 있는 기능을 제공합니다.

## 라이센스

이 프로젝트는 GNU General Public License v3.0(GPL-3.0) 라이센스 하에 배포됩니다.
자세한 내용은 [LICENSE](./LICENSE) 파일을 참조하세요.

## 라이브러리

ChzzkMC는 치지직의 비공식 자바 API 라이브러리 [chzzk4j](https://github.com/R2turnTrue/chzzk4j)를 포함합니다. ([chzzk4j-LICENSE](./chzzk4j-LICENSE.txt))

## 요구사항

- **Minecraft**: 1.21.5 이상
- **서버 소프트웨어**: Paper (또는 Paper 기반 포크)
- **Java**: 21 이상

## 기능

- **실시간 채팅 연동**: 치지직 채팅을 마인크래프트 서버로 실시간 전송
- **투표 시스템**: 2-4개 선택지로 구성 가능한 투표 기능. 보스바와 액션바를 통한 투표 현황 표시
- **API 지원**: 다른 플러그인에서 투표 제어

## 설정(config.yml)

```yaml
# 치지직 채널 ID (스트리밍 링크 마지막 부분)
channel-id: "1dcb975316a0be7bc445beecc29b9673"

# 치지직 채팅 출력 여부
broadcast-chat: false

display:
  # 남은 시간 표시 방식 (action_bar 또는 chat)
  timer: "action_bar"

vote:
  # 투표 선택지 개수 (2-4)
  option: 3
  # 투표 선택지 제목
  titles:
    - "1번에 투표하세요!"
    - "2번에 투표하세요!"
    - "3번에 투표하세요!"
    - "4번에 투표하세요!"
  # 투표 지속 시간 (초)
  durationSec: 120
  # 투표 결과를 발표할지 여부
  showingResult: true
```

## 사용법

### 명령어
- `/chzzkmc help` - 도움말 표시
- `/chzzkmc reload` - 설정 파일 재로드 및 채팅 재연결
- `/chzzkmc vote <start|end>` - 투표 시작 / 강제종료

### 권한
- `chzzkmc.use` (기본값: OP)

### 투표 참여 방법
시청자들은 치지직 채팅에서 다음 명령어로 투표에 참여할 수 있습니다:

- `!투표1` - 첫 번째 선택지에 투표
- `!투표2` - 두 번째 선택지에 투표
- `!투표3` - 세 번째 선택지에 투표
- `!투표4` - 네 번째 선택지에 투표

### 투표 시스템 특징
- **실시간 진행률**: 보스바를 통해 각 선택지별 득표율을 실시간으로 확인
- **카운트다운**: 액션바에 남은 투표 시간 표시
- **투표 변경**: 사용자는 투표를 변경할 수 있으며, 이전 투표는 자동으로 취소
- **자동 종료**: 설정된 시간이 경과하면 자동으로 투표가 종료되고 결과 발표

## API 사용법

다른 플러그인에서 ChzzkMc의 투표 기능을 사용할 수 있습니다. 
ChzzkMcProvider로 투표를 시작 및 종료하고, VoteEndEvent로 투표 결과를 확인할 수 있습니다.
자세한 내용은 [api](./src/main/java/io/github/kdy05/chzzkMc/api) 패키지를 참고해주세요.

### 설치
[JitPack](https://jitpack.io/#KDY05/ChzzkMc/)을 통하여 의존성을 추가할 수 있습니다.

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.KDY05:ChzzkMc:v1.0.2'
}
```

### ChzzkMcProvider 예제
```java
// ChzzkMc API 가져오기
ChzzkMcProvider provider = ChzzkMcProvider.getInstance();

// 투표 시작 (선택지 제목, 지속시간)
String[] options = {"빨간팀", "파란팀", "노란팀"};
boolean started = provider.startVote(options, 60);

// 투표 상태 확인
boolean isActive = provider.isVoteActive();

// 투표 강제 종료
boolean ended = provider.endVote();
```

### VoteEndEvent 예제
```java
@EventHandler
public void onVoteEnd(VoteEndEvent event) {
    int winningOption = event.getWinningOption();  // 1, 2, 3, 4
    int totalVotes = event.getTotalVotes();
    String[] titles = event.getOptionTitles();
    int[] voteCounts = event.getVoteCounts();
    
    // 투표 결과 처리
}
```

### AsyncChzzkChatEvent 예제
치지직 채팅 메시지를 비동기로 전달받을 수 있습니다.

```java
@EventHandler
public void onChzzkChat(AsyncChzzkChatEvent event) {
    String username = event.getUsername();  // 사용자명 (익명일 경우 "익명")
    String message = event.getMessage();    // 채팅 메시지 내용
    boolean isAnonymous = event.isAnonymous();  // 익명 사용자 여부
    
    // 특정 키워드 감지
    if (message.contains("안녕")) {
        // 비동기 이벤트에서 Bukkit API 사용시 메인 스레드로 스케줄링 필요
        Bukkit.getScheduler().runTask(plugin, () ->
                Bukkit.broadcast(Component.text(username + "님이 인사했습니다!"))
        );
    }
}
```
