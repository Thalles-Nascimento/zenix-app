import { Navigate, Outlet } from 'react-router-dom'

export default function RotaProtegida(){
    const token = localStorage.getItem("token");

    if (token === null){
        alert(token)
        return <Navigate to='/login'/>
    }
    
    return <Outlet/>;
}