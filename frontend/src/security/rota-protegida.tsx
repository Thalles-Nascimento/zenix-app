import { Navigate, Outlet } from 'react-router-dom'

export default function RotaProtegida(){
    const token = localStorage.getItem("token");

    if (token === null){
        return <Navigate to='/login'/>
    }
    
    return <Outlet/>;
}