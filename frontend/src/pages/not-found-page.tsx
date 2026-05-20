export default function NotFoundPage(){

    return (
        <div className="bg-black min-h-screen flex flex-col items-center justify-center">
            <a href="/login" className="bg-red-500 text-center block my-5 max-w-sm p-6 border border-default rounded-xl hover:bg-orange-900">
                <h5 className="mb-3 text-3xl font-bold tracking-tight text-white leading-8">404 NOT FOUND</h5>
                <p className="text-body font-semibold">Essa página não existe!</p>
            </a>
        </div>
    )
}