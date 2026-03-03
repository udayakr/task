import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useForgotPasswordMutation } from '@/api/authApi'
import { Link } from 'react-router-dom'

const schema = z.object({ email: z.string().email() })

export function ForgotPasswordPage() {
  const [forgotPassword, { isLoading, isSuccess }] = useForgotPasswordMutation()
  const { register, handleSubmit, formState: { errors } } = useForm<z.infer<typeof schema>>({ resolver: zodResolver(schema) })

  const onSubmit = async (data: z.infer<typeof schema>) => {
    await forgotPassword(data).unwrap()
  }

  if (isSuccess) return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <div className="text-center">
        <h2 className="text-xl font-bold mb-2">Check your inbox</h2>
        <p className="text-muted-foreground mb-4">If that email exists, a reset link has been sent.</p>
        <Link to="/login" className="text-primary hover:underline">Back to login</Link>
      </div>
    </div>
  )

  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <div className="w-full max-w-md">
        <h1 className="text-2xl font-bold mb-8 text-center">Reset your password</h1>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Email address</label>
            <input type="email" {...register('email')} className="w-full border border-input rounded-md px-3 py-2 text-sm" />
            {errors.email && <p className="text-destructive text-xs mt-1">{errors.email.message}</p>}
          </div>
          <button type="submit" disabled={isLoading}
            className="w-full bg-primary text-primary-foreground rounded-md py-2 font-medium disabled:opacity-50">
            {isLoading ? 'Sending…' : 'Send reset link'}
          </button>
        </form>
        <p className="text-center text-sm mt-6"><Link to="/login" className="text-primary hover:underline">Back to login</Link></p>
      </div>
    </div>
  )
}
