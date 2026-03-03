import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface UiState {
  darkMode: boolean
  sidebarOpen: boolean
}

const initialState: UiState = {
  darkMode: localStorage.getItem('darkMode') === 'true',
  sidebarOpen: true,
}

const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    toggleDarkMode(state) {
      state.darkMode = !state.darkMode
      localStorage.setItem('darkMode', String(state.darkMode))
      document.documentElement.classList.toggle('dark', state.darkMode)
    },
    setDarkMode(state, action: PayloadAction<boolean>) {
      state.darkMode = action.payload
      localStorage.setItem('darkMode', String(action.payload))
      document.documentElement.classList.toggle('dark', action.payload)
    },
    toggleSidebar(state) {
      state.sidebarOpen = !state.sidebarOpen
    },
  },
})

export const { toggleDarkMode, setDarkMode, toggleSidebar } = uiSlice.actions
export default uiSlice.reducer
