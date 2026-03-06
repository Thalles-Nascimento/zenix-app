import './App.css'
import { Routes, Route, Outlet } from 'react-router-dom'
import NotFoundPage from './pages/not-found-page'
import RotaProtegida from './security/rota-protegida'
import LoginPage from './pages/login-page'
import RotaAdmin from './security/rota-admin'
import Layout from './components/layout-component'
import LoginClient from './pages/login-fila-page'
import Atendimentos from './pages/atendimento-page'
import UsersPage from './pages/usuarios-page'
import FinanceiroPage from './pages/financeiro-page'
import DashboardPage from './pages/dashboard-page'
import UnidadesPage from './pages/unidade-page'
import FilaPage from './pages/fila-page'


function App() {
  return (
    <Routes>
      <Route path='/login' element={<LoginPage/>}/>
      <Route path='/fila/:unidadeId' element={<LoginClient/>}/>

      <Route path='/' element={<RotaProtegida/>}>
        <Route element={<Layout><Outlet /></Layout>}>
          <Route path='/atendimentos' element={<Atendimentos />} />
          <Route path='/usuarios' element={
            <RotaAdmin>
              <UsersPage/>
            </RotaAdmin>
          } />
          <Route path='/financeiro' element={<FinanceiroPage />}/>
          <Route path='/dashboard' element={
            <RotaAdmin>
                <DashboardPage />
            </RotaAdmin>
          } />
          <Route path='/unidades' element={
              <RotaAdmin>
                <UnidadesPage />
              </RotaAdmin>
          } />
          <Route path='/fila' element={<FilaPage />} />
        </Route>
      </Route>
      <Route path='/*' element={<NotFoundPage/>}/>
    </Routes>
    
    
  )
}    

export default App
