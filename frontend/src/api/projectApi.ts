import { apiSlice } from './apiSlice'
import { ApiResponse, PagedResponse, Project, ProjectStats, User } from '@/types'

export const projectApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getProjects: builder.query<ApiResponse<PagedResponse<Project>>, { page?: number; size?: number }>({
      query: ({ page = 0, size = 20 } = {}) => `/projects?page=${page}&size=${size}`,
      providesTags: ['Project'],
    }),
    getProject: builder.query<ApiResponse<Project>, string>({
      query: (id) => `/projects/${id}`,
      providesTags: (_r, _e, id) => [{ type: 'Project', id }],
    }),
    createProject: builder.mutation<ApiResponse<Project>, { name: string; description?: string }>({
      query: (body) => ({ url: '/projects', method: 'POST', body }),
      invalidatesTags: ['Project'],
    }),
    updateProject: builder.mutation<ApiResponse<Project>, { id: string; name: string; description?: string }>({
      query: ({ id, ...body }) => ({ url: `/projects/${id}`, method: 'PUT', body }),
      invalidatesTags: (_r, _e, { id }) => [{ type: 'Project', id }],
    }),
    archiveProject: builder.mutation<ApiResponse<null>, string>({
      query: (id) => ({ url: `/projects/${id}`, method: 'DELETE' }),
      invalidatesTags: ['Project'],
    }),
    getProjectMembers: builder.query<ApiResponse<User[]>, string>({
      query: (id) => `/projects/${id}/members`,
      providesTags: (_r, _e, id) => [{ type: 'Project', id }],
    }),
    addMember: builder.mutation<ApiResponse<null>, { projectId: string; userId: string }>({
      query: ({ projectId, userId }) => ({ url: `/projects/${projectId}/members/${userId}`, method: 'POST' }),
      invalidatesTags: (_r, _e, { projectId }) => [{ type: 'Project', id: projectId }],
    }),
    removeMember: builder.mutation<ApiResponse<null>, { projectId: string; userId: string }>({
      query: ({ projectId, userId }) => ({ url: `/projects/${projectId}/members/${userId}`, method: 'DELETE' }),
      invalidatesTags: (_r, _e, { projectId }) => [{ type: 'Project', id: projectId }],
    }),
    getProjectStats: builder.query<ApiResponse<ProjectStats>, string>({
      query: (id) => `/projects/${id}/stats`,
    }),
  }),
})

export const {
  useGetProjectsQuery, useGetProjectQuery, useCreateProjectMutation,
  useUpdateProjectMutation, useArchiveProjectMutation,
  useGetProjectMembersQuery, useAddMemberMutation, useRemoveMemberMutation,
  useGetProjectStatsQuery,
} = projectApi
