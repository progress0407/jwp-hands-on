## 요구사항

- [x] 0단계 - 휴리스틱 캐싱 제거하기
    - `Cache-Control: no-cache, private` 로 설정
    - `testNoCachePrivate()`
- [x] 1단계 - HTTP Compression 설정하기
    - gzip이 적용됐는지 테스트 코드에서 확인
        - `testCompression()`
    - [x] 웹 브라우저에서 HTTP 응답의 헤더를 직접 확인
- [x] 2단계 - ETag/If-None-Match 적용하기
    - 필터를 사용하여 /etag 경로만 ETag를 적용
    - `testETag()`
- [x] 3단계 - 캐시 무효화(Cache Busting)
    - js, css, ETag 에 캐싱 적용
    - `testCacheBustingOfStaticResources()`
