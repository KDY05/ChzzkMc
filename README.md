# ChzzkMc

> 치지직(Chzzk) 스트리밍 플랫폼과 마인크래프트 서버를 연동하는 Paper 플러그인

ChzzkMc는 한국의 스트리밍 플랫폼인 치지직의 채팅을 마인크래프트 서버에 실시간으로 연동하고, 시청자들이 채팅으로 투표에 참여할 수 있는 기능을 제공합니다.

## 기능

- **실시간 채팅 연동**: 치지직 채팅을 마인크래프트 서버로 실시간 전송
- **투표 시스템**: 2-4개 선택지로 구성 가능한 투표 기능
- **실시간 진행률 표시**: 보스바를 이용한 득표율 막대와 액션바 카운트다운
- **설정 기반**: config.yml을 통한 투표 설정 구성
- **API 지원**: 다른 플러그인에서 투표 제어

## 요구사항

- **Minecraft**: 1.21.5+
- **서버 소프트웨어**: Paper (또는 Paper 기반 포크)
- **Java**: 21+

## config.yml 설정

```yaml
# ChzzkMc 설정
channel-id: "1dcb975316a0be7bc445beecc29b9673"  # 치지직 채널 ID
broadcast-chat: false                           # 채팅 전송 여부
vote:
  option: 3                                     # 투표 선택지 개수 (2-4)
  titles:                                       # 투표 선택지 제목
    - "1번에 투표하세요!"
    - "2번에 투표하세요!"
    - "3번에 투표하세요!"
    - "4번에 투표하세요!"
  durationSec: 120                              # 투표 지속 시간 (초)
```

## 사용법

### 명령어

```
/chzzkmc help          # 도움말 표시
/chzzkmc reload        # 설정 파일 재로드 및 채팅 재연결
/chzzkmc vote start    # 투표 시작
/chzzkmc vote end      # 투표 강제 종료
```

### 권한
`chzzkmc.use` (기본값: OP)

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

### ChzzkMcProvider 사용

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

### OnVoteEnd 이벤트 처리

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
