import { apiSlice } from './apiSlice'
import { ApiResponse, DashboardSummary, Task } from '@/types'

export const dashboardApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getDashboardSummary: builder.query<ApiResponse<DashboardSummary>, void>({
      query: () => '/dashboard/summary',
      providesTags: ['Dashboard'],
    }),
    getUpcomingTasks: builder.query<ApiResponse<Task[]>, void>({
      query: () => '/dashboard/upcoming',
      providesTags: ['Dashboard'],
    }),
  }),
})

export const { useGetDashboardSummaryQuery, useGetUpcomingTasksQuery } = dashboardApi
