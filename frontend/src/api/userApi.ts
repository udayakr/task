import { apiSlice } from './apiSlice'
import { ApiResponse, PagedResponse, User } from '@/types'

export const userApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getMe: builder.query<ApiResponse<User>, void>({
      query: () => '/users/me',
      providesTags: ['User'],
    }),
    updateProfile: builder.mutation<ApiResponse<User>, { firstName: string; lastName: string }>({
      query: (body) => ({ url: '/users/me', method: 'PUT', body }),
      invalidatesTags: ['User'],
    }),
    changePassword: builder.mutation<ApiResponse<null>, { currentPassword: string; newPassword: string }>({
      query: (body) => ({ url: '/users/me/password', method: 'PUT', body }),
    }),
    getAllUsers: builder.query<ApiResponse<PagedResponse<User>>, { page?: number; size?: number } | void>({
      query: (params = {}) => {
        const { page = 0, size = 20 } = params ?? {}
        return `/users?page=${page}&size=${size}`
      },
      providesTags: ['User'],
    }),
    deactivateUser: builder.mutation<ApiResponse<null>, string>({
      query: (id) => ({ url: `/users/${id}/deactivate`, method: 'PUT' }),
      invalidatesTags: ['User'],
    }),
  }),
})

export const {
  useGetMeQuery, useUpdateProfileMutation, useChangePasswordMutation,
  useGetAllUsersQuery, useDeactivateUserMutation,
} = userApi
