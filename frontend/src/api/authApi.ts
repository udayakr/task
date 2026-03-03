import { apiSlice } from './apiSlice'
import { ApiResponse, AuthResponse, User } from '@/types'

export const authApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    register: builder.mutation<ApiResponse<User>, { email: string; password: string; firstName: string; lastName: string }>({
      query: (body) => ({ url: '/auth/register', method: 'POST', body }),
    }),
    login: builder.mutation<ApiResponse<AuthResponse>, { email: string; password: string }>({
      query: (body) => ({ url: '/auth/login', method: 'POST', body }),
      invalidatesTags: ['Dashboard'],
    }),
    logout: builder.mutation<ApiResponse<null>, { refreshToken?: string }>({
      query: (body) => ({ url: '/auth/logout', method: 'POST', body }),
    }),
    forgotPassword: builder.mutation<ApiResponse<null>, { email: string }>({
      query: (body) => ({ url: '/auth/forgot-password', method: 'POST', body }),
    }),
    resetPassword: builder.mutation<ApiResponse<null>, { token: string; newPassword: string }>({
      query: (body) => ({ url: '/auth/reset-password', method: 'POST', body }),
    }),
  }),
})

export const {
  useRegisterMutation, useLoginMutation, useLogoutMutation,
  useForgotPasswordMutation, useResetPasswordMutation,
} = authApi
