#  roles ADMIN y USER

## Usuarios de prueba (se crean solos al iniciar, si no existen)
| Usuario   | Contraseña | Rol   |
|-----------|------------|-------|
| `admin`   | `Admin1234`| ADMIN |
| `usuario` | `User1234` | USER  |

Los usuarios que se registran en `/registro` siempre quedan como `USER`.
Para volver ADMIN a otro: `UPDATE usuarios SET rol = 'ADMIN' WHERE username = 'roy';` y volver a iniciar sesión.
