import { useGetDashboardSummaryQuery, useGetUpcomingTasksQuery } from '@/api/dashboardApi'
import { CardSkeleton } from '@/components/LoadingSkeleton'
import { Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'
import { CheckCircle, Clock, AlertTriangle, ListTodo } from 'lucide-react'
import { format } from 'date-fns'

export function DashboardPage() {
  const { data: summaryData, isLoading: summaryLoading } = useGetDashboardSummaryQuery()
  const { data: upcomingData, isLoading: upcomingLoading } = useGetUpcomingTasksQuery()

  const summary = summaryData?.data
  const upcoming = upcomingData?.data ?? []

  const summaryCards = [
    { label: 'Total Tasks', value: summary?.totalTasks ?? 0, icon: <ListTodo className="w-5 h-5" />, color: 'text-blue-600' },
    { label: 'Completed', value: summary?.completedTasks ?? 0, icon: <CheckCircle className="w-5 h-5" />, color: 'text-green-600' },
    { label: 'In Progress', value: summary?.inProgressTasks ?? 0, icon: <Clock className="w-5 h-5" />, color: 'text-orange-600' },
    { label: 'Overdue', value: summary?.overdueTasks ?? 0, icon: <AlertTriangle className="w-5 h-5" />, color: 'text-red-600' },
  ]

  const pieData = [
    { name: 'Completed', value: summary?.completedTasks ?? 0 },
    { name: 'In Progress', value: summary?.inProgressTasks ?? 0 },
    { name: 'Overdue', value: summary?.overdueTasks ?? 0 },
    { name: 'Other', value: Math.max(0, (summary?.totalTasks ?? 0) - (summary?.completedTasks ?? 0) - (summary?.inProgressTasks ?? 0) - (summary?.overdueTasks ?? 0)) },
  ]

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Dashboard</h1>

      {summaryLoading ? (
        <div className="grid grid-cols-4 gap-4"><CardSkeleton /><CardSkeleton /><CardSkeleton /><CardSkeleton /></div>
      ) : (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {summaryCards.map((card) => (
            <div key={card.label} className="bg-card border border-border rounded-lg p-4 flex items-center gap-3">
              <div className={card.color}>{card.icon}</div>
              <div>
                <p className="text-2xl font-bold">{card.value}</p>
                <p className="text-sm text-muted-foreground">{card.label}</p>
              </div>
            </div>
          ))}
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-card border border-border rounded-lg p-4">
          <h2 className="font-semibold mb-4">Task Status Overview</h2>
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie data={pieData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={70} label>
                {pieData.map((_, i) => <Cell key={i} fill={['#22c55e','#f97316','#ef4444','#94a3b8'][i]} />)}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-card border border-border rounded-lg p-4">
          <h2 className="font-semibold mb-4">Upcoming Tasks (Next 7 Days)</h2>
          {upcomingLoading ? <CardSkeleton /> : upcoming.length === 0 ? (
            <p className="text-muted-foreground text-sm">No upcoming tasks.</p>
          ) : (
            <div className="space-y-2">
              {upcoming.slice(0, 8).map((task) => (
                <div key={task.id} className="flex items-center justify-between text-sm">
                  <span className="truncate flex-1">{task.title}</span>
                  <span className="text-muted-foreground ml-2 shrink-0">
                    {task.dueDate ? format(new Date(task.dueDate), 'MMM d') : ''}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
