import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Link, useNavigate } from 'react-router-dom'
import { useRegisterMutation } from '@/api/authApi'

const schema = z.object({
  firstName: z.string().min(1, 'Required').max(100),
  lastName: z.string().min(1, 'Required').max(100),
  email: z.string().email('Invalid email'),
  password: z.string().min(8, 'Min 8 characters')
    .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).+$/, 'Must include upper, lower, number, special char'),
})
type FormData = z.infer<typeof schema>

export function RegisterPage() {
  const navigate = useNavigate()
  const [register, { isLoading, error, isSuccess }] = useRegisterMutation()

  const { register: field, handleSubmit, formState: { errors } } = useForm<FormData>({ resolver: zodResolver(schema) })

  const onSubmit = async (data: FormData) => {
    try { await register(data).unwrap() } catch {}
  }

  const apiError = (error as { data?: { message?: string } })?.data?.message

  if (isSuccess) return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <div className="text-center">
        <h2 className="text-xl font-bold mb-2">Registration successful!</h2>
        <p className="text-muted-foreground mb-4">Check your inbox to verify your email.</p>
        <button onClick={() => navigate('/login')} className="text-primary hover:underline">Go to login</button>
      </div>
    </div>
  )

  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <div className="w-full max-w-md">
        <h1 className="text-2xl font-bold mb-8 text-center">Create your account</h1>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {apiError && <div className="bg-destructive/10 text-destructive text-sm p-3 rounded-md">{apiError}</div>}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium mb-1">First name</label>
              <input {...field('firstName')} className="w-full border border-input rounded-md px-3 py-2 text-sm" />
              {errors.firstName && <p className="text-destructive text-xs mt-1">{errors.firstName.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Last name</label>
              <input {...field('lastName')} className="w-full border border-input rounded-md px-3 py-2 text-sm" />
              {errors.lastName && <p className="text-destructive text-xs mt-1">{errors.lastName.message}</p>}
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Email</label>
            <input type="email" {...field('email')} className="w-full border border-input rounded-md px-3 py-2 text-sm" />
            {errors.email && <p className="text-destructive text-xs mt-1">{errors.email.message}</p>}
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Password</label>
            <input type="password" {...field('password')} className="w-full border border-input rounded-md px-3 py-2 text-sm" />
            {errors.password && <p className="text-destructive text-xs mt-1">{errors.password.message}</p>}
          </div>
          <button type="submit" disabled={isLoading}
            className="w-full bg-primary text-primary-foreground rounded-md py-2 font-medium hover:bg-primary/90 disabled:opacity-50">
            {isLoading ? 'Creating account…' : 'Create account'}
          </button>
        </form>
        <p className="text-center text-sm mt-6 text-muted-foreground">
          Already have an account? <Link to="/login" className="text-primary hover:underline">Sign in</Link>
        </p>
      </div>
    </div>
  )
}
