import { test, expect } from '@playwright/test'

test.describe('Authentication', () => {
  test('shows login page on unauthenticated access', async ({ page }) => {
    await page.goto('/dashboard')
    await expect(page).toHaveURL('/login')
  })

  test('can login with valid credentials', async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[type="email"]', 'admin@tms.com')
    await page.fill('input[type="password"]', 'Admin@1234')
    await page.click('button[type="submit"]')
    await expect(page).toHaveURL('/dashboard')
  })

  test('shows error on invalid credentials', async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[type="email"]', 'wrong@test.com')
    await page.fill('input[type="password"]', 'WrongPass1!')
    await page.click('button[type="submit"]')
    await expect(page.locator('.bg-destructive\\/10')).toBeVisible()
  })
})
