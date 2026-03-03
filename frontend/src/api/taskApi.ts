import { apiSlice } from './apiSlice'
import { ApiResponse, Comment, PagedResponse, Task, TaskPriority, TaskStatus } from '@/types'

interface TaskFilters {
  projectId: string
  status?: TaskStatus
  priority?: TaskPriority
  assigneeId?: string
  search?: string
  page?: number
  size?: number
}

interface CreateTaskBody {
  title: string
  description?: string
  status?: TaskStatus
  priority?: TaskPriority
  dueDate?: string
  assigneeId?: string
  tags?: string
  estimatedHours?: number
}

export const taskApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getTasks: builder.query<ApiResponse<PagedResponse<Task>>, TaskFilters>({
      query: ({ projectId, ...params }) => ({
        url: `/projects/${projectId}/tasks`,
        params: { page: 0, size: 100, ...params },
      }),
      providesTags: (_r, _e, { projectId }) => [{ type: 'Task', id: projectId }],
    }),
    getTask: builder.query<ApiResponse<Task>, { projectId: string; taskId: string }>({
      query: ({ projectId, taskId }) => `/projects/${projectId}/tasks/${taskId}`,
      providesTags: (_r, _e, { taskId }) => [{ type: 'Task', id: taskId }],
    }),
    createTask: builder.mutation<ApiResponse<Task>, { projectId: string } & CreateTaskBody>({
      query: ({ projectId, ...body }) => ({ url: `/projects/${projectId}/tasks`, method: 'POST', body }),
      invalidatesTags: (_r, _e, { projectId }) => [{ type: 'Task', id: projectId }, 'Dashboard'],
    }),
    updateTask: builder.mutation<ApiResponse<Task>, { projectId: string; taskId: string } & Partial<CreateTaskBody>>({
      query: ({ projectId, taskId, ...body }) => ({ url: `/projects/${projectId}/tasks/${taskId}`, method: 'PUT', body }),
      invalidatesTags: (_r, _e, { projectId, taskId }) => [{ type: 'Task', id: projectId }, { type: 'Task', id: taskId }],
    }),
    deleteTask: builder.mutation<ApiResponse<null>, { projectId: string; taskId: string }>({
      query: ({ projectId, taskId }) => ({ url: `/projects/${projectId}/tasks/${taskId}`, method: 'DELETE' }),
      invalidatesTags: (_r, _e, { projectId }) => [{ type: 'Task', id: projectId }, 'Dashboard'],
    }),
    updateTaskStatus: builder.mutation<ApiResponse<Task>, { projectId: string; taskId: string; status: TaskStatus }>({
      query: ({ projectId, taskId, status }) => ({ url: `/projects/${projectId}/tasks/${taskId}/status`, method: 'PATCH', body: { status } }),
      invalidatesTags: (_r, _e, { projectId, taskId }) => [{ type: 'Task', id: projectId }, { type: 'Task', id: taskId }, 'Dashboard'],
    }),
    assignTask: builder.mutation<ApiResponse<Task>, { projectId: string; taskId: string; assigneeId?: string }>({
      query: ({ projectId, taskId, assigneeId }) => ({ url: `/projects/${projectId}/tasks/${taskId}/assign`, method: 'PATCH', body: { assigneeId } }),
      invalidatesTags: (_r, _e, { projectId, taskId }) => [{ type: 'Task', id: projectId }, { type: 'Task', id: taskId }],
    }),
    getMyTasks: builder.query<ApiResponse<PagedResponse<Task>>, { page?: number; size?: number } | void>({
      query: (params = {}) => {
        const { page = 0, size = 50 } = params ?? {}
        return `/projects/00000000-0000-0000-0000-000000000000/tasks/my-tasks?page=${page}&size=${size}`
      },
      providesTags: ['Task'],
    }),
    getComments: builder.query<ApiResponse<PagedResponse<Comment>>, { projectId: string; taskId: string }>({
      query: ({ projectId, taskId }) => `/projects/${projectId}/tasks/${taskId}/comments`,
      providesTags: (_r, _e, { taskId }) => [{ type: 'Comment', id: taskId }],
    }),
    addComment: builder.mutation<ApiResponse<Comment>, { projectId: string; taskId: string; content: string }>({
      query: ({ projectId, taskId, content }) => ({ url: `/projects/${projectId}/tasks/${taskId}/comments`, method: 'POST', body: { content } }),
      invalidatesTags: (_r, _e, { taskId }) => [{ type: 'Comment', id: taskId }],
    }),
    deleteComment: builder.mutation<ApiResponse<null>, { projectId: string; taskId: string; commentId: string }>({
      query: ({ projectId, taskId, commentId }) => ({ url: `/projects/${projectId}/tasks/${taskId}/comments/${commentId}`, method: 'DELETE' }),
      invalidatesTags: (_r, _e, { taskId }) => [{ type: 'Comment', id: taskId }],
    }),
  }),
})

export const {
  useGetTasksQuery, useGetTaskQuery, useCreateTaskMutation,
  useUpdateTaskMutation, useDeleteTaskMutation, useUpdateTaskStatusMutation,
  useAssignTaskMutation, useGetMyTasksQuery,
  useGetCommentsQuery, useAddCommentMutation, useDeleteCommentMutation,
} = taskApi
