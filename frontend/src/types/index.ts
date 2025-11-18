/**
 * TypeScript 타입 정의
 */

export interface News {
  id: number
  title: string
  content?: string
  summary: string
  source: string
  url: string
  thumbnailUrl?: string
  publishedAt: string
  importanceScore?: number
}

export interface DailySummary {
  date: string
  formattedDate: string
  topNews: News[]
  totalNewsCount: number
}
