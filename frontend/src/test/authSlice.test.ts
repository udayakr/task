import { describe, it, expect } from 'vitest'
import authReducer, { setCredentials, logout } from '@/features/auth/authSlice'

const mockUser = { id: '1', email: 'test@test.com', firstName: 'Test', lastName: 'User', role: 'USER' as const, active: true, emailVerified: true, createdAt: '' }

describe('authSlice', () => {
  it('sets credentials on login', () => {
    const state = authReducer(undefined, setCredentials({ accessToken: 'at', refreshToken: 'rt', user: mockUser }))
    expect(state.isAuthenticated).toBe(true)
    expect(state.user?.email).toBe('test@test.com')
  })

  it('clears state on logout', () => {
    const loggedIn = authReducer(undefined, setCredentials({ accessToken: 'at', refreshToken: 'rt', user: mockUser }))
    const state = authReducer(loggedIn, logout())
    expect(state.isAuthenticated).toBe(false)
    expect(state.user).toBeNull()
  })
})
