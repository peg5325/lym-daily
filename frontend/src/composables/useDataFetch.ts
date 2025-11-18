import { ref, type Ref } from 'vue'

/**
 * DataFetchAgent (Composable)
 *
 * 책임: API 호출, 로딩/에러 상태 관리, 자동 재시도 로직
 */

interface UseFetchOptions {
  retryCount?: number
  retryDelay?: number
}

export function useDataFetch<T>(options: UseFetchOptions = {}) {
  const { retryCount = 3, retryDelay = 1000 } = options

  const data: Ref<T | null> = ref(null)
  const loading = ref(false)
  const error: Ref<string | null> = ref(null)

  /**
   * API 호출 (재시도 로직 포함)
   */
  const fetchData = async (url: string, retries = retryCount): Promise<void> => {
    loading.value = true
    error.value = null

    try {
      const response = await fetch(url)

      if (!response.ok) {
        throw new Error(`API 요청 실패: ${response.status}`)
      }

      const result = await response.json()
      data.value = result

    } catch (err) {
      console.error('API 호출 에러:', err)

      // 재시도 로직
      if (retries > 0) {
        console.log(`재시도 중... (남은 시도 횟수: ${retries})`)
        await new Promise(resolve => setTimeout(resolve, retryDelay))
        return fetchData(url, retries - 1)
      }

      error.value = err instanceof Error ? err.message : '알 수 없는 오류가 발생했습니다.'
    } finally {
      loading.value = false
    }
  }

  /**
   * 데이터 리셋
   */
  const reset = () => {
    data.value = null
    loading.value = false
    error.value = null
  }

  return {
    data,
    loading,
    error,
    fetchData,
    reset
  }
}
