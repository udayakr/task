import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useGetMeQuery, useUpdateProfileMutation, useChangePasswordMutation } from '@/api/userApi'
import { useAppDispatch } from '@/hooks/useAppDispatch'
import { updateUser } from '@/features/auth/authSlice'
import { CardSkeleton } from '@/components/LoadingSkeleton'
import { useState } from 'react'

const profileSchema = z.object({ firstName: z.string().min(1).max(100), lastName: z.string().min(1).max(100) })
const passwordSchema = z.object({
  currentPassword: z.string().min(1),
  newPassword: z.string().min(8).regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).+$/, 'Must include upper, lower, number, special char'),
})

type ProfileForm = z.infer<typeof profileSchema>
type PasswordForm = z.infer<typeof passwordSchema>

export function ProfilePage() {
  const dispatch = useAppDispatch()
  const { data, isLoading } = useGetMeQuery()
  const [updateProfile, { isLoading: updating }] = useUpdateProfileMutation()
  const [changePassword, { isLoading: changingPw }] = useChangePasswordMutation()
  const [pwSuccess, setPwSuccess] = useState(false)

  const profileForm = useForm<ProfileForm>({ resolver: zodResolver(profileSchema), values: { firstName: data?.data.firstName ?? '', lastName: data?.data.lastName ?? '' } })
  const passwordForm = useForm<PasswordForm>({ resolver: zodResolver(passwordSchema) })

  const onProfileSubmit = async (values: ProfileForm) => {
    const result = await updateProfile(values).unwrap()
    dispatch(updateUser(result.data))
  }

  const onPasswordSubmit = async (values: PasswordForm) => {
    await changePassword(values).unwrap()
    passwordForm.reset()
    setPwSuccess(true)
    setTimeout(() => setPwSuccess(false), 3000)
  }

  if (isLoading) return <div className="p-6"><CardSkeleton /></div>

  const user = data?.data

  return (
    <div className="p-6 max-w-xl space-y-8">
      <h1 className="text-2xl font-bold">Profile</h1>

      <div className="bg-card border border-border rounded-lg p-6 space-y-4">
        <h2 className="font-semibold">Personal Information</h2>
        <p className="text-sm text-muted-foreground">{user?.email}</p>
        <form onSubmit={profileForm.handleSubmit(onProfileSubmit)} className="space-y-3">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium mb-1">First name</label>
              <input {...profileForm.register('firstName')} className="w-full border border-input rounded-md px-3 py-2 text-sm" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Last name</label>
              <input {...profileForm.register('lastName')} className="w-full border border-input rounded-md px-3 py-2 text-sm" />
            </div>
          </div>
          <button type="submit" disabled={updating}
            className="bg-primary text-primary-foreground px-4 py-1.5 rounded-md text-sm disabled:opacity-50">
            {updating ? 'Saving…' : 'Save changes'}
          </button>
        </form>
      </div>

      <div className="bg-card border border-border rounded-lg p-6 space-y-4">
        <h2 className="font-semibold">Change Password</h2>
        {pwSuccess && <p className="text-green-600 text-sm">Password changed successfully!</p>}
        <form onSubmit={passwordForm.handleSubmit(onPasswordSubmit)} className="space-y-3">
          <div>
            <label className="block text-sm font-medium mb-1">Current password</label>
            <input type="password" {...passwordForm.register('currentPassword')} className="w-full border border-input rounded-md px-3 py-2 text-sm" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">New password</label>
            <input type="password" {...passwordForm.register('newPassword')} className="w-full border border-input rounded-md px-3 py-2 text-sm" />
            {passwordForm.formState.errors.newPassword && (
              <p className="text-destructive text-xs mt-1">{passwordForm.formState.errors.newPassword.message}</p>
            )}
          </div>
          <button type="submit" disabled={changingPw}
            className="bg-primary text-primary-foreground px-4 py-1.5 rounded-md text-sm disabled:opacity-50">
            {changingPw ? 'Changing…' : 'Change password'}
          </button>
        </form>
      </div>
    </div>
  )
}
